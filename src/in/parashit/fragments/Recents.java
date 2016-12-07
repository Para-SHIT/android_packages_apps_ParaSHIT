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

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class Recents extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String IMMERSIVE_RECENTS = "immersive_recents";
    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";

    private ListPreference mImmersiveRecents;
    private ListPreference mRecentsClearAllLocation;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PARASHIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.recents_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        mImmersiveRecents = (ListPreference) findPreference(IMMERSIVE_RECENTS);
        mImmersiveRecents.setValue(String.valueOf(Settings.System.getInt(
                resolver, Settings.System.IMMERSIVE_RECENTS, 0)));
        mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
        mImmersiveRecents.setOnPreferenceChangeListener(this);

        mRecentsClearAllLocation = (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 4,
                UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry());
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mImmersiveRecents) {
            Settings.System.putInt(resolver, Settings.System.IMMERSIVE_RECENTS,
                    Integer.parseInt((String) newValue));
            mImmersiveRecents.setValue((String) newValue);
            mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
            return true;
        } else if (preference == mRecentsClearAllLocation) {
            int location = Integer.parseInt((String) newValue);
            int index = mRecentsClearAllLocation.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location,
                    UserHandle.USER_CURRENT);
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]);
            return true;
        }
        return false;
    }
}
