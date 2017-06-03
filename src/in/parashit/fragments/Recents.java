/*
 * Copyright (C) 2017 The ParaSHIT Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.parashit.fragments;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v14.preference.SwitchPreference;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.internal.util.parashit.PackageUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class Recents extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String RECENTS_USE_OMNISWITCH = "recents_use_omniswitch";
    private static final String OMNISWITCH_START_SETTINGS = "omniswitch_start_settings";
    public static final String OMNISWITCH_PACKAGE_NAME = "org.omnirom.omniswitch";
    public static Intent INTENT_OMNISWITCH_SETTINGS = new Intent(Intent.ACTION_MAIN).setClassName(OMNISWITCH_PACKAGE_NAME,
            OMNISWITCH_PACKAGE_NAME + ".SettingsActivity");
    private static final String RECENTS_USE_SLIM= "use_slim_recents";
    private static final String CATEGORY_STOCK_RECENTS = "stock_recents";
    private static final String CATEGORY_OMNI_RECENTS = "omni_recents";
    private static final String CATEGORY_SLIM_RECENTS = "slim_recents";

    private ListPreference mRecentsClearAllLocation;
    private Preference mOmniSwitchSettings;
    private PreferenceCategory mOmniRecents;
    private PreferenceCategory mSlimRecents;
    private PreferenceCategory mStockRecents;
    private SwitchPreference mRecentsUseOmniSwitch;
    private SwitchPreference mRecentsUseSlim;

    private boolean mOmniSwitchInitCalled;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PARASHIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.recents_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        mStockRecents = (PreferenceCategory) findPreference(CATEGORY_STOCK_RECENTS);
        mOmniRecents = (PreferenceCategory) findPreference(CATEGORY_OMNI_RECENTS);
        mSlimRecents = (PreferenceCategory) findPreference(CATEGORY_SLIM_RECENTS);

        mRecentsClearAllLocation = (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 4,
                UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry());
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);

        mRecentsUseOmniSwitch = (SwitchPreference) findPreference(RECENTS_USE_OMNISWITCH);
        try {
            mRecentsUseOmniSwitch.setChecked(Settings.System.getInt(resolver,
                    Settings.System.RECENTS_USE_OMNISWITCH) == 1);
            mOmniSwitchInitCalled = true;
        } catch(SettingNotFoundException e){
            // if the settings value is unset
        }
        mRecentsUseOmniSwitch.setOnPreferenceChangeListener(this);

        mOmniSwitchSettings = (Preference) findPreference(OMNISWITCH_START_SETTINGS);
        mOmniSwitchSettings.setEnabled(mRecentsUseOmniSwitch.isChecked());

        mRecentsUseSlim = (SwitchPreference) findPreference(RECENTS_USE_SLIM);
        mRecentsUseSlim.setOnPreferenceChangeListener(this);
        updateRecents();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mRecentsClearAllLocation) {
            int location = Integer.parseInt((String) newValue);
            int index = mRecentsClearAllLocation.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location,
                    UserHandle.USER_CURRENT);
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]);
            return true;
        } else if (preference == mRecentsUseOmniSwitch) {
            boolean value = (Boolean) newValue;
            // if value has never been set before
            if (value && !mOmniSwitchInitCalled) {
                openOmniSwitchFirstTimeWarning();
                mOmniSwitchInitCalled = true;
            }
            Settings.System.putInt(resolver,
                    Settings.System.RECENTS_USE_OMNISWITCH, value ? 1 : 0);
            mOmniSwitchSettings.setEnabled(value);
            updateRecents();
            return true;
        } else if (preference == mRecentsUseSlim) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.USE_SLIM_RECENTS, value ? 1 : 0);
            updateRecents();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mOmniSwitchSettings) {
            startActivity(INTENT_OMNISWITCH_SETTINGS);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    private boolean isOmniSwitchInstalled() {
        return PackageUtils.isAvailableApp(OMNISWITCH_PACKAGE_NAME, getActivity());
    }

    private void openOmniSwitchFirstTimeWarning() {
        new AlertDialog.Builder(getActivity())
            .setTitle(getResources().getString(R.string.omniswitch_first_time_title))
            .setMessage(getResources().getString(R.string.omniswitch_first_time_message))
            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
            }).show();
    }

    private void updateRecents() {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean omniRecents = Settings.System.getInt(resolver,
                Settings.System.RECENTS_USE_OMNISWITCH, 0) == 1;
        boolean slimRecents = Settings.System.getInt(resolver,
                Settings.System.USE_SLIM_RECENTS, 0) == 1;

        mStockRecents.setEnabled(!omniRecents && !slimRecents);
        // Slim recents overwrites omni recents
        mOmniRecents.setEnabled(isOmniSwitchInstalled() && (omniRecents || !slimRecents));
        // Don't allow OmniSwitch if we're already using slim recents
        mSlimRecents.setEnabled(slimRecents || !omniRecents);
    }
}
