package com.bluewhaleyt.codewhale.fragments.preferences.application;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.bluewhaleyt.codewhale.WhaleApplication;
import com.bluewhaleyt.codewhale.activities.BaseActivity;
import com.bluewhaleyt.codewhale.activities.MainActivity;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.common.SDKUtil;
import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.bluewhaleyt.codewhale.R;
import com.jakewharton.processphoenix.ProcessPhoenix;

public class ApplicationFragment extends CustomPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_application, rootKey);
        initialize();
    }

    private void initialize() {

        try {

            var prefLanguage = findPreference("pref_app_language");
            var prefTheme = findPreference("pref_app_theme");
            var prefDynamicColor = findPreference("pref_enable_dynamic_color");

            var btnPrefComponent = findPreference("btn_pref_app_component");

            restartApp(prefDynamicColor);
            restartApp(prefLanguage);

            prefTheme.setOnPreferenceChangeListener((preference, newValue) -> {
                ProcessPhoenix.triggerRebirth(requireContext());
                return true;
            });

            btnPrefComponent.setOnPreferenceClickListener(preference -> {
                IntentUtil.intentFragment(requireActivity(), new ComponentFragment());
                return true;
            });

        } catch (Exception e) {
            SnackbarUtil.makeErrorSnackbar(requireActivity(), e.getMessage(), e.toString());
        }

    }

    private void restartApp(Preference pref) {
        pref.setOnPreferenceClickListener(preference -> {
            SnackbarUtil snackbar = new SnackbarUtil(requireActivity(), getString(R.string.settings_change));
            snackbar.setAction(getString(R.string.restart), v -> ProcessPhoenix.triggerRebirth(requireContext()));
            return true;
        });
    }

    private void updateTheme(int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
    }

}
