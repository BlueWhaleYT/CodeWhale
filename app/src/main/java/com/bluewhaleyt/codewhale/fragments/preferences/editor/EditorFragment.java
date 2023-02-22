package com.bluewhaleyt.codewhale.fragments.preferences.editor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;

import com.bluewhaleyt.common.CommonUtil;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;

public class EditorFragment extends CustomPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_editor, rootKey);
        initialize();
    }

    private void initialize() {
        try {

            var prefTextmate = findPreference("pref_enable_textmate");
            var prefFontSize = findPreference("pref_font_size");

            var btnPrefTheme = findPreference("btn_pref_theme");
            var btnPrefLanguage = findPreference("btn_pref_syntax_language");
            var btnPrefComponent = findPreference("btn_pref_component");

            var categoryPrefLanguage = findPreference("category_pref_syntax_language");

            intentFragment(btnPrefTheme, new ThemeFragment());
            intentFragment(btnPrefLanguage, new LanguageFragment());
            intentFragment(btnPrefComponent, new ComponentFragment());

            // disable language btn if textmate is not applied
            disableLanguageBtn(btnPrefLanguage);
            prefTextmate.setOnPreferenceClickListener(preference -> {
                disableLanguageBtn(btnPrefLanguage);
                return true;
            });

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

    private void disableLanguageBtn(Preference pref) {
        var categoryPrefLanguage = findPreference("category_pref_syntax_language");
        pref.setVisible(!PreferencesManager.isTextmateEnabled());
        categoryPrefLanguage.setVisible(!PreferencesManager.isTextmateEnabled());
    }

}
