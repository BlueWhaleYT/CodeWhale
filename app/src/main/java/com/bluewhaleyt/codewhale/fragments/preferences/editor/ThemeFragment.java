package com.bluewhaleyt.codewhale.fragments.preferences.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.bluewhaleyt.codeeditor.textmate.syntaxhighlight.SyntaxHighlightUtil;
import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.tools.editor.basic.ThemeHandler;
import com.bluewhaleyt.codewhale.tools.editor.basic.languages.LanguageNameHandler;
import com.bluewhaleyt.codewhale.utils.AssetsFileLoader;
import com.bluewhaleyt.codewhale.utils.Constants;
import com.bluewhaleyt.codewhale.utils.EditorUtil;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.codewhale.utils.SharedPrefsUtil;
import com.bluewhaleyt.component.preferences.CustomPreferenceFragment;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;

import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.widget.CodeEditor;

public class ThemeFragment extends CustomPreferenceFragment {

    private SharedPrefsUtil sharedPrefsUtil;
    private EditorUtil editorUtil;

    private TextInputLayout menuDropdownTheme, menuDropDownLanguage;
    private AutoCompleteTextView autoCompleteTextViewTheme, autoCompleteTextViewLanguage;

    private BottomSheetDialog bottomSheetDialog;

    private String selectedLanguage = "Java";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_editor_theme, rootKey);
    }

    @Override
    public void onStart() {
        super.onStart();
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
        bottomSheetDialog = new BottomSheetDialog(requireActivity());
        var v = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_layout_choose_theme, null);
        CodeEditor editor = v.findViewById(R.id.editor);
        bottomSheetDialog.setContentView(v);
        bottomSheetDialog.create();
        disableDragBottomSheetDialog();

        editorUtil = new EditorUtil(requireActivity(), editor, editor.getColorScheme());

        int themeType;
        if (PreferencesManager.isTextmateEnabled()) {
            showTextMateThemes(v, editor, pref);
            menuDropDownLanguage.setVisibility(View.VISIBLE);
            themeType = ThemeHandler.THEME_TEXTMATE;
        } else {
            showNormalThemes(v, editor, pref);
            menuDropDownLanguage.setVisibility(View.GONE);
            themeType = ThemeHandler.THEME_NORMAL;
        }
        ThemeHandler.setTheme(requireContext(), editor, PreferencesManager.getEditorTheme(), themeType, Constants.TEST_SYNTAX);

        editorUtil.setup();
        editorUtil.setNonPrintFlag();
        editor.setEditable(false);
        editor.setTextSize(11);
        setPresetText();

        v.findViewById(R.id.fabClose).setOnClickListener(view -> bottomSheetDialog.dismiss());

    }

    private void set(CodeEditor editor, Preference pref, int themeType) {
        ((AutoCompleteTextView) menuDropdownTheme.getEditText()).setOnItemClickListener((adapterView, view, position, id) -> {
            var selectedTheme = menuDropdownTheme.getEditText().getText().toString();
            selectedLanguage = menuDropDownLanguage.getEditText().getText().toString();

            if (selectedTheme.equals(getString(R.string.custom))) {
                Toast.makeText(requireContext(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
            } else {
                ThemeHandler.setTheme(requireContext(), editor, selectedTheme, themeType, "." + LanguageNameHandler.getLanguageCode(selectedLanguage));
                pref.setSummary(selectedTheme);
                saveTheme(selectedTheme);
                editorUtil.setTextActionWindow(requireContext(), editor.getColorScheme());
            }
        });

        ((AutoCompleteTextView) menuDropDownLanguage.getEditText()).setOnItemClickListener((adapterView, view, position, id) -> {
            var selectedTheme = menuDropdownTheme.getEditText().getText().toString();
            selectedLanguage = menuDropDownLanguage.getEditText().getText().toString();
            ThemeHandler.setTheme(requireContext(), editor, selectedTheme, themeType, "." + LanguageNameHandler.getLanguageCode(selectedLanguage));
            setPresetText();
        });
    }

    private void basicSetupTheme(View v, CodeEditor editor, Preference pref, String[] array, int themeType) {
        menuDropdownTheme = v.findViewById(R.id.menu_dropdown_theme);
        menuDropDownLanguage = v.findViewById(R.id.menu_dropdown_language);
        autoCompleteTextViewTheme = v.findViewById(R.id.auto_complete_tv_theme);

        set(editor, pref, themeType);

        var adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, array);
        autoCompleteTextViewTheme.setAdapter(adapter);
    }

    private void basicSetupLanguage(View v, CodeEditor editor, Preference pref, String[] array, int themeType) {
        menuDropdownTheme = v.findViewById(R.id.menu_dropdown_theme);
        menuDropDownLanguage = v.findViewById(R.id.menu_dropdown_language);
        autoCompleteTextViewLanguage = v.findViewById(R.id.auto_complete_tv_language);

        set(editor, pref, themeType);

        var adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, array);
        autoCompleteTextViewLanguage.setAdapter(adapter);
    }

    private void showNormalThemes(View v, CodeEditor editor, Preference pref) {
        String[] themes = getContext().getResources().getStringArray(R.array.normal_editor_theme);
        basicSetupTheme(v, editor, pref, themes, ThemeHandler.THEME_NORMAL);

        autoCompleteTextViewTheme.setText(PreferencesManager.getEditorTheme(), false);
        editor.setEditorLanguage(new JavaLanguage());

    }

    private void showTextMateThemes(View v, CodeEditor editor, Preference pref) {
        var themes = getContext().getResources().getStringArray(R.array.textmate_editor_theme);
        var languages = getContext().getResources().getStringArray(R.array.textmate_editor_language);
        basicSetupTheme(v, editor, pref, themes, ThemeHandler.THEME_TEXTMATE);
        basicSetupLanguage(v, editor, pref, languages, ThemeHandler.THEME_TEXTMATE);

        autoCompleteTextViewTheme.setText(PreferencesManager.getEditorTheme(), false);
        autoCompleteTextViewLanguage.setText(selectedLanguage, false);
    }

    private void saveTheme(String theme) {
        sharedPrefsUtil = new SharedPrefsUtil(requireContext(), "pref_editor_theme", theme);
        sharedPrefsUtil.saveData();
    }

    private void setPresetText() {
        var file = LanguageNameHandler.getLanguageCode(selectedLanguage);
        editorUtil.setText(AssetsFileLoader.getAssetsFileContent(requireContext(), "presets/main." + file));
    }

    private void disableDragBottomSheetDialog() {
        var behavior = bottomSheetDialog.getBehavior();
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

}
