package com.bluewhaleyt.codewhale.fragments.preferences.editor;

import android.os.Bundle;

import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;
import com.bluewhaleyt.codewhale.R;

public class ThemeFragment extends CustomPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_editor_theme, rootKey);
        initialize();
    }

    private void initialize() {

    }

}
