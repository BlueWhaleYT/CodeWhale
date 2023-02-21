package com.bluewhaleyt.codewhale.fragments.preferences.about;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.fragments.preferences.application.ApplicationFragment;
import com.bluewhaleyt.codewhale.fragments.preferences.editor.EditorFragment;
import com.bluewhaleyt.codewhale.utils.Constants;
import com.bluewhaleyt.common.ApplicationUtil;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;

public class AboutFragment extends CustomPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_about, rootKey);
        init();
    }

    private void init() {
        try {

            var prefOpenSource = findPreference("btn_pref_open_source");
            var prefDeveloper = findPreference("btn_pref_developer");
            var prefTranslate = findPreference("btn_pref_translate");
            var prefVersion = findPreference("btn_pref_version");

            prefOpenSource.setOnPreferenceClickListener(preference -> {
                IntentUtil.intentURL(requireActivity(), Constants.PROJECT_GITHUB_REPOSITORY);
                return true;
            });

            prefDeveloper.setSummary(Constants.PROJECT_DEVELOPER);
            prefTranslate.setSummary(getString(R.string.translator));
            prefVersion.setSummary(ApplicationUtil.getAppVersionName(requireActivity()));

        } catch (Exception e) {
            SnackbarUtil.makeErrorSnackbar(requireActivity(), e.getMessage(), e.toString());
        }

    }

}