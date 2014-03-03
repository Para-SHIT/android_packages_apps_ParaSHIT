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
import android.widget.Toast;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class Animations extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String POWER_MENU_ANIMATION = "power_menu_animation";
    private static final String PREF_TILE_ANIM_STYLE = "qs_tile_animation_style";
    private static final String PREF_TILE_ANIM_DURATION = "qs_tile_animation_duration";
    private static final String PREF_TILE_ANIM_INTERPOLATOR = "qs_tile_animation_interpolator";
    private static final String PREF_TOAST_ANIMATION = "toast_animation";

    private ListPreference mPowerMenuAnimation;
    private ListPreference mTileAnimationStyle;
    private ListPreference mTileAnimationDuration;
    private ListPreference mTileAnimationInterpolator;
    private ListPreference mToastAnimation;

    Toast mToast;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PARASHIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.animations_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        mPowerMenuAnimation = (ListPreference) findPreference(POWER_MENU_ANIMATION);
        int powerMenuAnimation = Settings.System.getInt(resolver,
                Settings.System.POWER_MENU_ANIMATION, 0);
        mPowerMenuAnimation.setValue(String.valueOf(powerMenuAnimation));
        mPowerMenuAnimation.setSummary(mPowerMenuAnimation.getEntry());
        mPowerMenuAnimation.setOnPreferenceChangeListener(this);

        mTileAnimationStyle = (ListPreference) findPreference(PREF_TILE_ANIM_STYLE);
        int tileAnimationStyle = Settings.System.getIntForUser(resolver,
                Settings.System.ANIM_TILE_STYLE, 0,
                UserHandle.USER_CURRENT);
        mTileAnimationStyle.setValue(String.valueOf(tileAnimationStyle));
        mTileAnimationStyle.setSummary(mTileAnimationStyle.getEntry());
        mTileAnimationStyle.setOnPreferenceChangeListener(this);

        mTileAnimationDuration = (ListPreference) findPreference(PREF_TILE_ANIM_DURATION);
        int tileAnimationDuration = Settings.System.getIntForUser(resolver,
                Settings.System.ANIM_TILE_DURATION, 2000,
                UserHandle.USER_CURRENT);
        mTileAnimationDuration.setValue(String.valueOf(tileAnimationDuration));
        mTileAnimationDuration.setSummary(mTileAnimationDuration.getEntry());
        mTileAnimationDuration.setEnabled(tileAnimationStyle > 0);
        mTileAnimationDuration.setOnPreferenceChangeListener(this);

        mTileAnimationInterpolator = (ListPreference) findPreference(PREF_TILE_ANIM_INTERPOLATOR);
        int tileAnimationInterpolator = Settings.System.getIntForUser(resolver,
                Settings.System.ANIM_TILE_INTERPOLATOR, 0,
                UserHandle.USER_CURRENT);
        mTileAnimationInterpolator.setValue(String.valueOf(tileAnimationInterpolator));
        mTileAnimationInterpolator.setSummary(mTileAnimationInterpolator.getEntry());
        mTileAnimationInterpolator.setEnabled(tileAnimationStyle > 0);
        mTileAnimationInterpolator.setOnPreferenceChangeListener(this);

        mToastAnimation = (ListPreference) findPreference(PREF_TOAST_ANIMATION);
        mToastAnimation.setSummary(mToastAnimation.getEntry());
        int CurrentToastAnimation = Settings.System.getInt(resolver,
                Settings.System.TOAST_ANIMATION, 1);
        mToastAnimation.setValueIndex(CurrentToastAnimation); //set to index of default value
        mToastAnimation.setSummary(mToastAnimation.getEntries()[CurrentToastAnimation]);
        mToastAnimation.setOnPreferenceChangeListener(this);

        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int value;
        int index;
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mPowerMenuAnimation) {
            value = Integer.parseInt((String) newValue);
            index = mPowerMenuAnimation.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.POWER_MENU_ANIMATION, value);
            mPowerMenuAnimation.setSummary(mPowerMenuAnimation.getEntries()[index]);
            return true;
        } else if (preference == mTileAnimationStyle) {
            value = Integer.parseInt((String) newValue);
            index = mTileAnimationStyle.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.ANIM_TILE_STYLE, value,
                    UserHandle.USER_CURRENT);
            mTileAnimationStyle.setSummary(mTileAnimationStyle.getEntries()[index]);
            mTileAnimationDuration.setEnabled(value > 0);
            mTileAnimationInterpolator.setEnabled(value > 0);
            return true;
        } else if (preference == mTileAnimationDuration) {
            value = Integer.parseInt((String) newValue);
            index = mTileAnimationDuration.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.ANIM_TILE_DURATION, value,
                    UserHandle.USER_CURRENT);
            mTileAnimationDuration.setSummary(mTileAnimationDuration.getEntries()[index]);
            return true;
        } else if (preference == mTileAnimationInterpolator) {
            value = Integer.parseInt((String) newValue);
            index = mTileAnimationInterpolator.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.ANIM_TILE_INTERPOLATOR, value,
                    UserHandle.USER_CURRENT);
            mTileAnimationInterpolator.setSummary(mTileAnimationInterpolator.getEntries()[index]);
            return true;
        } else if (preference == mToastAnimation) {
            index = mToastAnimation.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.TOAST_ANIMATION, index);
            mToastAnimation.setSummary(mToastAnimation.getEntries()[index]);
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getActivity(), mToastAnimation.getEntries()[index],
                    Toast.LENGTH_SHORT);
            mToast.show();
            return true;
        }
        return false;
    }
}
