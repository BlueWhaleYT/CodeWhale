package com.bluewhaleyt.codewhale.fragments.preferences.editor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.fragments.preferences.about.AboutFragment;
import com.bluewhaleyt.codewhale.fragments.preferences.application.ApplicationFragment;
import com.bluewhaleyt.codewhale.fragments.preferences.codingstyle.CodingStyleFragment;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;

public class ComponentFragment extends CustomPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_editor_component, rootKey);
        init();
    }

    private void init() {
        try {

        } catch (Exception e) {
            SnackbarUtil.makeErrorSnackbar(requireActivity(), e.getMessage(), e.toString());
        }

    }

}
