/*
 * Copyright (C) 2014-2016 The Dirty Unicorns Project
 *           (C) 2017 The ParaSHIT Project
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

package in.parashit.tabs;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.internal.util.parashit.PackageUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class MultiTasking extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String KEY_OMNISWITCH = "omniswitch";
    private static final String KEY_OMNISWITCH_PACKAGE_NAME = "org.omnirom.omniswitch";

    private PreferenceScreen mOmniSwitch;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PARASHIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.multitasking);

        ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();

        mOmniSwitch = (PreferenceScreen) findPreference(KEY_OMNISWITCH);
        if (mOmniSwitch != null && !isOmniSwitchInstalled()) {
            prefSet.removePreference(mOmniSwitch);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return true;
    }

    private boolean isOmniSwitchInstalled() {
        return PackageUtils.isAvailableApp(KEY_OMNISWITCH_PACKAGE_NAME, getActivity());
    }
}
