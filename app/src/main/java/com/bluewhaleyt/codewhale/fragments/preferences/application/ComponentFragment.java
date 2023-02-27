package com.bluewhaleyt.codewhale.fragments.preferences.application;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.fragments.preferences.editor.LanguageFragment;
import com.bluewhaleyt.codewhale.fragments.preferences.editor.ThemeFragment;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;

public class ComponentFragment extends CustomPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_application_component, rootKey);
        initialize();
    }

    private void initialize() {
        try {

        } catch (Exception e) {
            SnackbarUtil.makeErrorSnackbar(requireActivity(), e.getMessage(), e.toString());
        }
    }

}
