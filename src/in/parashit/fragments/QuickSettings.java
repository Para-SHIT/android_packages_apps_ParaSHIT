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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import in.parashit.preference.SeekBarPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuickSettings extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String DAYLIGHT_HEADER_PACK = "daylight_header_pack";
    private static final String DEFAULT_HEADER_PACKAGE = "com.android.systemui";
    private static final String CUSTOM_HEADER_IMAGE_SHADOW = "status_bar_custom_header_shadow";
    private static final String CUSTOM_HEADER_PROVIDER = "custom_header_provider";
    private static final String CUSTOM_HEADER_BROWSE = "custom_header_browse";
    private static final String PREF_ROWS_PORTRAIT = "qs_rows_portrait";
    private static final String PREF_ROWS_LANDSCAPE = "qs_rows_landscape";
    private static final String PREF_COLUMNS_PORTRAIT = "qs_columns_portrait";
    private static final String PREF_COLUMNS_LANDSCAPE = "qs_columns_landscape";

    private ListPreference mDaylightHeaderPack;
    private ListPreference mHeaderProvider;
    private PreferenceScreen mHeaderBrowse;
    private SeekBarPreference mHeaderShadow;
    private String mDaylightHeaderProvider;
    private SeekBarPreference mRowsPortrait;
    private SeekBarPreference mRowsLandscape;
    private SeekBarPreference mQsColumnsPortrait;
    private SeekBarPreference mQsColumnsLandscape;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PARASHIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.qs_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        // Custom Header options
        String settingHeaderPackage = Settings.System.getString(resolver,
                Settings.System.STATUS_BAR_DAYLIGHT_HEADER_PACK);
        if (settingHeaderPackage == null) {
            settingHeaderPackage = DEFAULT_HEADER_PACKAGE;
        }
        mDaylightHeaderPack = (ListPreference) findPreference(DAYLIGHT_HEADER_PACK);

        List<String> entries = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        getAvailableHeaderPacks(entries, values);
        mDaylightHeaderPack.setEntries(entries.toArray(new String[entries.size()]));
        mDaylightHeaderPack.setEntryValues(values.toArray(new String[values.size()]));

        int valueIndex = mDaylightHeaderPack.findIndexOfValue(settingHeaderPackage);
        if (valueIndex == -1) {
            settingHeaderPackage = DEFAULT_HEADER_PACKAGE;
            Settings.System.putString(resolver,
                    Settings.System.STATUS_BAR_DAYLIGHT_HEADER_PACK, settingHeaderPackage);
            valueIndex = mDaylightHeaderPack.findIndexOfValue(settingHeaderPackage);
        }
        mDaylightHeaderPack.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mDaylightHeaderPack.setSummary(mDaylightHeaderPack.getEntry());
        mDaylightHeaderPack.setOnPreferenceChangeListener(this);

        mHeaderShadow = (SeekBarPreference) findPreference(CUSTOM_HEADER_IMAGE_SHADOW);
        final int headerShadow = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, 80);
        mHeaderShadow.setValue(headerShadow);
        mHeaderShadow.setOnPreferenceChangeListener(this);

        mDaylightHeaderProvider = getResources().getString(R.string.daylight_header_provider);
        String providerName = Settings.System.getString(resolver,
                Settings.System.STATUS_BAR_CUSTOM_HEADER_PROVIDER);
        if (providerName == null) {
            providerName = mDaylightHeaderProvider;
        }
        mHeaderProvider = (ListPreference) findPreference(CUSTOM_HEADER_PROVIDER);
        valueIndex = mHeaderProvider.findIndexOfValue(providerName);
        mHeaderProvider.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mHeaderProvider.setSummary(mHeaderProvider.getEntry());
        mHeaderProvider.setOnPreferenceChangeListener(this);
        mDaylightHeaderPack.setEnabled(providerName.equals(mDaylightHeaderProvider));

        mHeaderBrowse = (PreferenceScreen) findPreference(CUSTOM_HEADER_BROWSE);
        mHeaderBrowse.setEnabled(isBrowseHeaderAvailable());

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

        mQsColumnsPortrait = (SeekBarPreference) findPreference(PREF_COLUMNS_PORTRAIT);
        int columnsQsPortrait = Settings.Secure.getInt(resolver,
                Settings.Secure.QS_COLUMNS_PORTRAIT, 3);
        mQsColumnsPortrait.setValue(columnsQsPortrait);
        mQsColumnsPortrait.setOnPreferenceChangeListener(this);

        mQsColumnsLandscape = (SeekBarPreference) findPreference(PREF_COLUMNS_LANDSCAPE);
        int columnsQsLandscape = Settings.Secure.getInt(resolver,
                Settings.Secure.QS_COLUMNS_LANDSCAPE, 3);
        mQsColumnsLandscape.setValue(columnsQsLandscape);
        mQsColumnsLandscape.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mDaylightHeaderPack) {
            String value = (String) newValue;
            Settings.System.putString(resolver,
                    Settings.System.STATUS_BAR_DAYLIGHT_HEADER_PACK, value);
            int valueIndex = mDaylightHeaderPack.findIndexOfValue(value);
            mDaylightHeaderPack.setSummary(mDaylightHeaderPack.getEntries()[valueIndex]);
            return true;
        } else if (preference == mHeaderShadow) {
            Integer headerShadow = (Integer) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, headerShadow);
            return true;
        } else if (preference == mHeaderProvider) {
            String value = (String) newValue;
            Settings.System.putString(resolver,
                    Settings.System.STATUS_BAR_CUSTOM_HEADER_PROVIDER, value);
            int valueIndex = mHeaderProvider.findIndexOfValue(value);
            mHeaderProvider.setSummary(mHeaderProvider.getEntries()[valueIndex]);
            mDaylightHeaderPack.setEnabled(value.equals(mDaylightHeaderProvider));
            return true;
        } else if (preference == mRowsPortrait) {
            intValue = (Integer) newValue;
            Settings.Secure.putInt(resolver,
                    Settings.Secure.QS_ROWS_PORTRAIT, intValue);
            return true;
        } else if (preference == mRowsLandscape) {
            intValue = (Integer) newValue;
            Settings.Secure.putInt(resolver,
                    Settings.Secure.QS_ROWS_LANDSCAPE, intValue);
            return true;
        } else if (preference == mQsColumnsPortrait) {
            intValue = (Integer) newValue;
            Settings.Secure.putInt(resolver,
                    Settings.Secure.QS_COLUMNS_PORTRAIT, intValue);
            return true;
        } else if (preference == mQsColumnsLandscape) {
            intValue = (Integer) newValue;
            Settings.Secure.putInt(resolver,
                    Settings.Secure.QS_COLUMNS_LANDSCAPE, intValue);
            return true;
        }
        return false;
    }

    private void getAvailableHeaderPacks(List<String> entries, List<String> values) {
        String defaultLabel = null;
        Map<String, String> headerMap = new HashMap<String, String>();
        Intent i = new Intent();
        PackageManager packageManager = getPackageManager();
        i.setAction("org.omnirom.DaylightHeaderPack");
        for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
            String packageName = r.activityInfo.packageName;
            String label = r.activityInfo.loadLabel(getPackageManager()).toString();
            if (label == null) {
                label = r.activityInfo.packageName;
            }
            if (packageName.equals(DEFAULT_HEADER_PACKAGE)) {
                defaultLabel = label;
            } else {
                headerMap.put(label, packageName);
            }
        }
        i.setAction("org.omnirom.DaylightHeaderPack1");
        for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
            String packageName = r.activityInfo.packageName;
            String label = r.activityInfo.loadLabel(getPackageManager()).toString();
            if (label == null) {
                label = packageName;
            }
            headerMap.put(label, packageName  + "/" + r.activityInfo.name);
        }
        List<String> labelList = new ArrayList<String>();
        labelList.addAll(headerMap.keySet());
        Collections.sort(labelList);
        for (String label : labelList) {
            entries.add(label);
            values.add(headerMap.get(label));
        }
        entries.add(0, defaultLabel);
        values.add(0, DEFAULT_HEADER_PACKAGE);
    }

    private boolean isBrowseHeaderAvailable() {
        PackageManager pm = getPackageManager();
        Intent browse = new Intent();
        browse.setClassName("org.omnirom.omnistyle", "org.omnirom.omnistyle.BrowseHeaderActivity");
        return pm.resolveActivity(browse, 0) != null;
    }
}
