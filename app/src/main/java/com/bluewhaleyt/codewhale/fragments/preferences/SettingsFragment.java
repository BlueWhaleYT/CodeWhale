package com.bluewhaleyt.codewhale.fragments.preferences;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;

import com.bluewhaleyt.codewhale.fragments.preferences.about.AboutFragment;
import com.bluewhaleyt.codewhale.fragments.preferences.codingstyle.CodeStyleFragment;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.fragments.preferences.application.ApplicationFragment;
import com.bluewhaleyt.codewhale.fragments.preferences.editor.EditorFragment;

public class SettingsFragment extends CustomPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey);
        init();
    }

    private void init() {
        try {

            var btnPrefApp = findPreference("btn_pref_application");
            var btnPrefEditor = findPreference("btn_pref_editor");
            var btnPrefCodeStyle = findPreference("btn_pref_coding_style");
            var btnPrefAbout = findPreference("btn_pref_about");

            intentFragment(btnPrefApp, new ApplicationFragment());
            intentFragment(btnPrefEditor, new EditorFragment());
            intentFragment(btnPrefCodeStyle, new CodeStyleFragment());
            intentFragment(btnPrefAbout, new AboutFragment());

        } catch (Exception e) {
            SnackbarUtil.makeErrorSnackbar(requireActivity(), e.getMessage(), e.toString());
        }

    }

    private void intentFragment(Preference pref, Fragment fragment) {
        pref.setOnPreferenceClickListener(preference -> {
            IntentUtil.intentFragment(requireActivity(), fragment);
            return true;
        });
    }

}
