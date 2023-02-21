package com.bluewhaleyt.codewhale.fragments.preferences.codingstyle;

import android.os.Bundle;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.jakewharton.processphoenix.ProcessPhoenix;

public class CodingStyleFragment extends CustomPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_codingstyle, rootKey);
        initialize();
    }

    private void initialize() {

        try {



        } catch (Exception e) {
            SnackbarUtil.makeErrorSnackbar(requireActivity(), e.getMessage(), e.toString());
        }

    }

}
