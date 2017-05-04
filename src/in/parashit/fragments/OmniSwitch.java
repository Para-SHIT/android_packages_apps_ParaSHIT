/*
 * Copyright (C) 2016-2017 The ParaSHIT Project
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
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class OmniSwitch extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String RECENTS_USE_OMNISWITCH = "recents_use_omniswitch";
    private static final String OMNISWITCH_START_SETTINGS = "omniswitch_start_settings";

    // Package name of the omnniswitch app
    public static final String OMNISWITCH_PACKAGE_NAME = "org.omnirom.omniswitch";
    // Intent for launching the omniswitch settings actvity
    public static Intent INTENT_OMNISWITCH_SETTINGS = new Intent(Intent.ACTION_MAIN).setClassName(OMNISWITCH_PACKAGE_NAME,
                                OMNISWITCH_PACKAGE_NAME + ".SettingsActivity");

    private Preference mOmniSwitchSettings;
    private boolean mOmniSwitchInitCalled;
    private SwitchPreference mRecentsUseOmniSwitch;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PARASHIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.omniswitch);

        ContentResolver resolver = getActivity().getContentResolver();

        // OmniRecents
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

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mRecentsUseOmniSwitch) {
            boolean value = (Boolean) newValue;
            // if value has never been set before
            if (value && !mOmniSwitchInitCalled) {
                openOmniSwitchFirstTimeWarning();
                mOmniSwitchInitCalled = true;
            }
            Settings.System.putInt(resolver,
                    Settings.System.RECENTS_USE_OMNISWITCH, value ? 1 : 0);
            mOmniSwitchSettings.setEnabled(value);
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

    private void openOmniSwitchFirstTimeWarning() {
        new AlertDialog.Builder(getActivity())
            .setTitle(getResources().getString(R.string.omniswitch_first_time_title))
            .setMessage(getResources().getString(R.string.omniswitch_first_time_message))
            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
            }).show();
    }
}
