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
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import in.parashit.preference.SeekBarPreference;

public class QuickSettings extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String PREF_ROWS_PORTRAIT = "qs_rows_portrait";
    private static final String PREF_ROWS_LANDSCAPE = "qs_rows_landscape";
    private static final String PREF_COLUMNS = "qs_columns";

    private SeekBarPreference mRowsPortrait;
    private SeekBarPreference mRowsLandscape;
    private SeekBarPreference mQsColumns;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PARASHIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.qs_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        mRowsPortrait = (SeekBarPreference) findPreference(PREF_ROWS_PORTRAIT);
        int rowsPortrait = Settings.Secure.getInt(resolver,
                Settings.Secure.QS_ROWS_PORTRAIT, 3);
        mRowsPortrait.setValue(rowsPortrait);
        mRowsPortrait.setOnPreferenceChangeListener(this);

        mRowsLandscape = (SeekBarPreference) findPreference(PREF_ROWS_LANDSCAPE);
        int defaultValue = getResources().getInteger(com.android.internal.R.integer.config_qs_num_rows_landscape_default);
        int rowsLandscape = Settings.Secure.getInt(resolver,
                Settings.Secure.QS_ROWS_LANDSCAPE, defaultValue);
        mRowsLandscape.setValue(rowsLandscape);
        mRowsLandscape.setOnPreferenceChangeListener(this);

        mQsColumns = (SeekBarPreference) findPreference(PREF_COLUMNS);
        int columnsQs = Settings.Secure.getInt(resolver,
                Settings.Secure.QS_COLUMNS, 3);
        mQsColumns.setValue(columnsQs);
        mQsColumns.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mRowsPortrait) {
            intValue = (Integer) newValue;
            Settings.Secure.putInt(resolver,
                    Settings.Secure.QS_ROWS_PORTRAIT, intValue);
            return true;
        } else if (preference == mRowsLandscape) {
            intValue = (Integer) newValue;
            Settings.Secure.putInt(resolver,
                    Settings.Secure.QS_ROWS_LANDSCAPE, intValue);
            return true;
        } else if (preference == mQsColumns) {
            intValue = (Integer) newValue;
            Settings.Secure.putInt(resolver,
                    Settings.Secure.QS_COLUMNS, intValue);
            return true;
        }
        return false;
    }
}
