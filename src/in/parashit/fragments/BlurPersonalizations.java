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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import in.parashit.preference.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class BlurPersonalizations extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    //Switch Preferences
    private SwitchPreference mExpand;
    private SwitchPreference mNotiTrans;
    private SwitchPreference mQuickSett;
    private SwitchPreference mRecentsSett;

    //Transluency,Radius and Scale
    private SeekBarPreference mScale;
    private SeekBarPreference mRadius;
    private SeekBarPreference mRecentsRadius;
    private SeekBarPreference mRecentsScale;
    private SeekBarPreference mQuickSettPerc;
    private SeekBarPreference mNotSettPerc;

    //Colors
    private ColorPickerPreference mDarkBlurColor;
    private ColorPickerPreference mLightBlurColor;
    private ColorPickerPreference mMixedBlurColor;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    public static int BLUR_LIGHT_COLOR_PREFERENCE_DEFAULT = Color.DKGRAY;
    public static int BLUR_MIXED_COLOR_PREFERENCE_DEFAULT = Color.GRAY;
    public static int BLUR_DARK_COLOR_PREFERENCE_DEFAULT = Color.LTGRAY;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PARASHIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.blur_personalizations);
        ContentResolver resolver = getActivity().getContentResolver();

        // Some help here
        int intLightColor;
        int intDarkColor;
        int intMixedColor;
        String hexLightColor;
        String hexDarkColor;
        String hexMixedColor;

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
                Settings.System.BLUR_LIGHT_COLOR_PREFERENCE_KEY, BLUR_LIGHT_COLOR_PREFERENCE_DEFAULT);
        hexLightColor = String.format("#%08x", (0xffffffff & intLightColor));
        mLightBlurColor.setSummary(hexLightColor);
        mLightBlurColor.setNewPreviewColor(intLightColor);
        mLightBlurColor.setOnPreferenceChangeListener(this);

        mDarkBlurColor = (ColorPickerPreference) findPreference("blur_dark_color");
        intDarkColor = Settings.System.getInt(resolver,
                Settings.System.BLUR_DARK_COLOR_PREFERENCE_KEY, BLUR_DARK_COLOR_PREFERENCE_DEFAULT);
        hexDarkColor = String.format("#%08x", (0xffffffff & intDarkColor));
        mDarkBlurColor.setSummary(hexDarkColor);
        mDarkBlurColor.setNewPreviewColor(intDarkColor);
        mDarkBlurColor.setOnPreferenceChangeListener(this);

        mMixedBlurColor = (ColorPickerPreference) findPreference("blur_mixed_color");
        intMixedColor = Settings.System.getInt(resolver,
                Settings.System.BLUR_MIXED_COLOR_PREFERENCE_KEY, BLUR_MIXED_COLOR_PREFERENCE_DEFAULT);
        hexMixedColor = String.format("#%08x", (0xffffffff & intMixedColor));
        mMixedBlurColor.setSummary(hexMixedColor);
        mMixedBlurColor.setNewPreviewColor(intMixedColor);
        mMixedBlurColor.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(com.android.internal.R.drawable.ic_menu_refresh)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
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
                Settings.System.TRANSLUCENT_QUICK_SETTINGS_PERCENTAGE_PREFERENCE_KEY, value);
            return true;
        /*} else if (preference == mNotSettPerc) {
            value = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                Settings.System.TRANSLUCENT_NOTIFICATIONS_PERCENTAGE_PREFERENCE_KEY, value);
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
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.BLUR_LIGHT_COLOR_PREFERENCE_KEY, intHex);
            return true;
        } else if (preference == mDarkBlurColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.parseInt(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.BLUR_DARK_COLOR_PREFERENCE_KEY, intHex);
            return true;
        } else if (preference == mMixedBlurColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.parseInt(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.BLUR_MIXED_COLOR_PREFERENCE_KEY, intHex);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if  (preference == mExpand) {
            boolean enabled = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_ENABLED_PREFERENCE_KEY, enabled ? 1:0);
        /*} else if (preference == mNotiTrans) {
            boolean enabled = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.TRANSLUCENT_NOTIFICATIONS_PREFERENCE_KEY, enabled ? 1:0);*/
        } else if (preference == mQuickSett) {
            boolean enabled = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.TRANSLUCENT_QUICK_SETTINGS_PREFERENCE_KEY, enabled ? 1:0);
        } else if (preference == mRecentsSett) {
            boolean enabled = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENT_APPS_ENABLED_PREFERENCE_KEY, enabled ? 1:0);
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        BlurPersonalizations getOwner() {
            return (BlurPersonalizations) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.blur_colors_reset_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getActivity().getContentResolver(),
                                Settings.System.BLUR_LIGHT_COLOR_PREFERENCE_KEY,
                                    BLUR_LIGHT_COLOR_PREFERENCE_DEFAULT);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                Settings.System.BLUR_DARK_COLOR_PREFERENCE_KEY,
                                    BLUR_DARK_COLOR_PREFERENCE_DEFAULT);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                Settings.System.BLUR_MIXED_COLOR_PREFERENCE_KEY,
                                    BLUR_MIXED_COLOR_PREFERENCE_DEFAULT);
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    };
}
