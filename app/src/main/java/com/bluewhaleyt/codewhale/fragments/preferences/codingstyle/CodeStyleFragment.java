package com.bluewhaleyt.codewhale.fragments.preferences.codingstyle;

import android.os.Bundle;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;

public class CodeStyleFragment extends CustomPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_codestyle, rootKey);
        initialize();
    }

    private void initialize() {

        try {



        } catch (Exception e) {
            SnackbarUtil.makeErrorSnackbar(requireActivity(), e.getMessage(), e.toString());
        }

    }

}
