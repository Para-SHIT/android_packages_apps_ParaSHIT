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
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class Animations extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PARASHIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.animations_settings);

        ContentResolver resolver = getActivity().getContentResolver();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }
}
