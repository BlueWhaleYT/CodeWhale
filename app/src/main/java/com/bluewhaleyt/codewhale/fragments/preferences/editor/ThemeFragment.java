package com.bluewhaleyt.codewhale.fragments.preferences.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.preference.Preference;

import com.bluewhaleyt.codeeditor.textmate.syntaxhighlight.SyntaxHighlightUtil;
import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.tools.editor.basic.ThemeHandler;
import com.bluewhaleyt.codewhale.utils.AssetsFileLoader;
import com.bluewhaleyt.codewhale.utils.Constants;
import com.bluewhaleyt.codewhale.utils.EditorUtil;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.codewhale.utils.SharedPrefsUtil;
import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;

import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.widget.CodeEditor;

public class ThemeFragment extends CustomPreferenceFragment {

    private SharedPrefsUtil sharedPrefsUtil;

    private TextInputLayout menuDropdown;
    private AutoCompleteTextView autoCompleteTextView;

    private BottomSheetDialog bottomSheetDialog;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_editor_theme, rootKey);
        initialize();
    }

    private void initialize() {

        try {

            var btnPrefTheme = findPreference("btn_pref_editor_theme");
            btnPrefTheme.setSummary(PreferencesManager.getEditorTheme());

            showThemeDialog(btnPrefTheme);
            btnPrefTheme.setOnPreferenceClickListener(preference -> {
                bottomSheetDialog.show();
                return true;
            });

        } catch (Exception e) {
            SnackbarUtil.makeErrorSnackbar(requireActivity(), e.getMessage(), e.toString());
        }

    }

    private void showThemeDialog(Preference pref) {
        var v = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_layout_choose_theme, null);

        CodeEditor editor = v.findViewById(R.id.editor);
        EditorUtil editorUtil = new EditorUtil(requireActivity(), editor);
        editorUtil.setup();
        editorUtil.setText(AssetsFileLoader.getAssetsFileContent(requireActivity(), "presets/Main.java"));
        editor.setEditable(false);
        editor.setScalable(false);
        editor.setTextSize(11);

        bottomSheetDialog = new BottomSheetDialog(requireActivity());
        bottomSheetDialog.setContentView(v);
        bottomSheetDialog.create();

        int themeType;
        if (PreferencesManager.isTextmateEnabled()) {
            showTextMateThemes(v, editor, pref);
            themeType = ThemeHandler.THEME_TEXTMATE;
        } else {
            showNormalThemes(v, editor, pref);
            themeType = ThemeHandler.THEME_NORMAL;
        }
        ThemeHandler.setTheme(requireContext(), editor, PreferencesManager.getEditorTheme(), themeType);

    }

    private void basicSetup(View v, CodeEditor editor, Preference pref, String[] array, int themeType) {
        menuDropdown = v.findViewById(R.id.menu_dropdown);
        autoCompleteTextView = v.findViewById(R.id.auto_complete_tv);

        ((AutoCompleteTextView) menuDropdown.getEditText()).setOnItemClickListener((adapterView, view, position, id) -> {
            var selectedTheme = menuDropdown.getEditText().getText().toString();
            ThemeHandler.setTheme(requireContext(), editor, selectedTheme, themeType);
            pref.setSummary(selectedTheme);
            saveTheme(selectedTheme);
        });

        var adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, array);
        autoCompleteTextView.setAdapter(adapter);
    }

    private void showNormalThemes(View v, CodeEditor editor, Preference pref) {
        String[] themes = getContext().getResources().getStringArray(R.array.normal_editor_theme);
        basicSetup(v, editor, pref, themes, ThemeHandler.THEME_NORMAL);

        autoCompleteTextView.setText(PreferencesManager.getEditorTheme(), false);
        editor.setEditorLanguage(new JavaLanguage());

    }

    private void showTextMateThemes(View v, CodeEditor editor, Preference pref) {
        String[] themes = getContext().getResources().getStringArray(R.array.textmate_editor_theme);
        basicSetup(v, editor, pref, themes, ThemeHandler.THEME_TEXTMATE);

        autoCompleteTextView.setText(PreferencesManager.getEditorTheme(), false);
    }

    private void saveTheme(String theme) {
        sharedPrefsUtil = new SharedPrefsUtil(requireContext(), "pref_editor_theme", theme);
        sharedPrefsUtil.saveData();
    }

}
