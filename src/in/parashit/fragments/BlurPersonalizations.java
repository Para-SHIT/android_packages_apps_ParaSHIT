/*
 * Copyright (C) 2016 The Xperia Open Source Project
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

package in.parashit.fragments;

import android.content.ContentResolver;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import in.parashit.preference.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class BlurPersonalizations extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static int BLUR_LIGHT_COLOR_DEFAULT = Color.DKGRAY;
    private static int BLUR_MIXED_COLOR_DEFAULT = Color.GRAY;
    private static int BLUR_DARK_COLOR_DEFAULT = Color.LTGRAY;

    private ColorPickerPreference mDarkBlurColor;
    private ColorPickerPreference mLightBlurColor;
    private ColorPickerPreference mMixedBlurColor;
    private SeekBarPreference mScale;
    private SeekBarPreference mRadius;
    private SeekBarPreference mRecentsRadius;
    private SeekBarPreference mRecentsScale;
    private SeekBarPreference mQuickSettPerc;
    private SeekBarPreference mNotSettPerc;
    private SwitchPreference mExpand;
    private SwitchPreference mNotiTrans;
    private SwitchPreference mQuickSett;
    private SwitchPreference mRecentsSett;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PARASHIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.blur_personalizations);

        int intLightColor;
        int intDarkColor;
        int intMixedColor;
        String hexLightColor;
        String hexDarkColor;
        String hexMixedColor;
        ContentResolver resolver = getActivity().getContentResolver();

        mExpand = (SwitchPreference) findPreference("blurred_status_bar_expanded_enabled_pref");
        mExpand.setChecked((Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_EXPANDED_ENABLED_PREFERENCE_KEY, 0) == 1));

        mScale = (SeekBarPreference) findPreference("statusbar_blur_scale");
        mScale.setValue(Settings.System.getInt(resolver,
                Settings.System.BLUR_SCALE_PREFERENCE_KEY, 10));
        mScale.setOnPreferenceChangeListener(this);

        mRadius = (SeekBarPreference) findPreference("statusbar_blur_radius");
        mRadius.setValue(Settings.System.getInt(resolver,
                Settings.System.BLUR_RADIUS_PREFERENCE_KEY, 5));
        mRadius.setOnPreferenceChangeListener(this);

        /*mNotiTrans = (SwitchPreference) findPreference("translucent_notifications_pref");
        mNotiTrans.setChecked((Settings.System.getInt(resolver,
                Settings.System.TRANSLUCENT_NOTIFICATIONS_PREFERENCE_KEY, 0) == 1));*/

        mQuickSett = (SwitchPreference) findPreference("translucent_quick_settings_pref");
        mQuickSett.setChecked((Settings.System.getInt(resolver,
                Settings.System.TRANSLUCENT_QUICK_SETTINGS_PREFERENCE_KEY, 0) == 1));

        mQuickSettPerc = (SeekBarPreference) findPreference("quick_settings_transluency");
        mQuickSettPerc.setValue(Settings.System.getInt(resolver,
                Settings.System.TRANSLUCENT_QUICK_SETTINGS_PERCENTAGE_PREFERENCE_KEY, 60));
        mQuickSettPerc.setOnPreferenceChangeListener(this);

        /*mNotSettPerc = (SeekBarPreference) findPreference("notifications_transluency");
        mNotSettPerc.setValue(Settings.System.getInt(resolver,
                Settings.System.TRANSLUCENT_NOTIFICATIONS_PERCENTAGE_PREFERENCE_KEY, 60));
        mNotSettPerc.setOnPreferenceChangeListener(this);*/

        mRecentsSett = (SwitchPreference) findPreference("blurred_recent_app_enabled_pref");
        mRecentsSett.setChecked((Settings.System.getInt(resolver,
                Settings.System.RECENT_APPS_ENABLED_PREFERENCE_KEY, 0) == 1));

        mRecentsScale = (SeekBarPreference) findPreference("recents_blur_scale");
        mRecentsScale.setValue(Settings.System.getInt(resolver,
                Settings.System.RECENT_APPS_SCALE_PREFERENCE_KEY, 6));
        mRecentsScale.setOnPreferenceChangeListener(this);

        mRecentsRadius = (SeekBarPreference) findPreference("recents_blur_radius");
        mRecentsRadius.setValue(Settings.System.getInt(resolver,
                Settings.System.RECENT_APPS_RADIUS_PREFERENCE_KEY, 3));
        mRecentsRadius.setOnPreferenceChangeListener(this);

        mLightBlurColor = (ColorPickerPreference) findPreference("blur_light_color");
        intLightColor = Settings.System.getInt(resolver,
                Settings.System.BLUR_LIGHT_COLOR_PREFERENCE_KEY, BLUR_LIGHT_COLOR_DEFAULT);
        hexLightColor = String.format("#%08x", (0xffffffff & intLightColor));
        if (hexLightColor.equals("#ff444444")) {
            mLightBlurColor.setSummary(R.string.default_string);
        } else {
            mLightBlurColor.setSummary(hexLightColor);
        }
        mLightBlurColor.setNewPreviewColor(intLightColor);
        mLightBlurColor.setOnPreferenceChangeListener(this);

        mDarkBlurColor = (ColorPickerPreference) findPreference("blur_dark_color");
        intDarkColor = Settings.System.getInt(resolver,
                Settings.System.BLUR_DARK_COLOR_PREFERENCE_KEY, BLUR_DARK_COLOR_DEFAULT);
        hexDarkColor = String.format("#%08x", (0xffffffff & intDarkColor));
        if (hexDarkColor.equals("#ffcccccc")) {
            mDarkBlurColor.setSummary(R.string.default_string);
        } else {
            mDarkBlurColor.setSummary(hexDarkColor);
        }
        mDarkBlurColor.setNewPreviewColor(intDarkColor);
        mDarkBlurColor.setOnPreferenceChangeListener(this);

        mMixedBlurColor = (ColorPickerPreference) findPreference("blur_mixed_color");
        intMixedColor = Settings.System.getInt(resolver,
                Settings.System.BLUR_MIXED_COLOR_PREFERENCE_KEY, BLUR_MIXED_COLOR_DEFAULT);
        hexMixedColor = String.format("#%08x", (0xffffffff & intMixedColor));
        if (hexMixedColor.equals("#ff888888")) {
            mMixedBlurColor.setSummary(R.string.default_string);
        } else {
            mMixedBlurColor.setSummary(hexMixedColor);
        }
        mMixedBlurColor.setNewPreviewColor(intMixedColor);
        mMixedBlurColor.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int value;
        int intHex;
        String hex;
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mScale) {
            value = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.BLUR_SCALE_PREFERENCE_KEY, value);
            return true;
        } else if (preference == mRadius) {
            value = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.BLUR_RADIUS_PREFERENCE_KEY, value);
            return true;
        } else if (preference == mQuickSettPerc) {
            value = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.TRANSLUCENT_QUICK_SETTINGS_PERCENTAGE_PREFERENCE_KEY,
                            value);
            return true;
        /*} else if (preference == mNotSettPerc) {
            value = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.TRANSLUCENT_NOTIFICATIONS_PERCENTAGE_PREFERENCE_KEY,
                            value);
            return true;
        }*/
        } else if (preference == mRecentsScale) {
            value = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.RECENT_APPS_SCALE_PREFERENCE_KEY, value);
            return true;
        } else if(preference == mRecentsRadius) {
            value = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.RECENT_APPS_RADIUS_PREFERENCE_KEY, value);
            return true;
        } else if (preference == mLightBlurColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.parseInt(String.valueOf(newValue)));
            if (hex.equals("#ff444444")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.BLUR_LIGHT_COLOR_PREFERENCE_KEY, intHex);
            return true;
        } else if (preference == mDarkBlurColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.parseInt(String.valueOf(newValue)));
            if (hex.equals("#ffcccccc")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.BLUR_DARK_COLOR_PREFERENCE_KEY, intHex);
            return true;
        } else if (preference == mMixedBlurColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.parseInt(String.valueOf(newValue)));
            if (hex.equals("#ff888888")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.BLUR_MIXED_COLOR_PREFERENCE_KEY, intHex);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mExpand) {
            boolean enabled = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_EXPANDED_ENABLED_PREFERENCE_KEY,
                            enabled ? 1 : 0);
        /*} else if (preference == mNotiTrans) {
            boolean enabled = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(resolver,
                    Settings.System.TRANSLUCENT_NOTIFICATIONS_PREFERENCE_KEY,
                            enabled ? 1 : 0);*/
        } else if (preference == mQuickSett) {
            boolean enabled = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(resolver,
                    Settings.System.TRANSLUCENT_QUICK_SETTINGS_PREFERENCE_KEY,
                            enabled ? 1 : 0);
        } else if (preference == mRecentsSett) {
            boolean enabled = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(resolver,
                    Settings.System.RECENT_APPS_ENABLED_PREFERENCE_KEY,
                            enabled ? 1 : 0);
        }
        return super.onPreferenceTreeClick(preference);
    }
}
