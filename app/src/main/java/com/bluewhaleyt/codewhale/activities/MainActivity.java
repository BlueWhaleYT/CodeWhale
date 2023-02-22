package com.bluewhaleyt.codewhale.activities;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;

import com.bluewhaleyt.codeeditor.textmate.syntaxhighlight.SyntaxHighlightUtil;
import com.bluewhaleyt.codewhale.tools.editor.basic.languages.JavaLanguage;
import com.bluewhaleyt.codewhale.tools.editor.basic.languages.modules.AndroidJavaLanguage;
import com.bluewhaleyt.codewhale.tools.editor.textmate.CustomSyntaxHighlighter;
import com.bluewhaleyt.codewhale.utils.AssetsFileLoader;
import com.bluewhaleyt.codewhale.utils.EditorUtil;
import com.bluewhaleyt.common.CommonUtil;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.databinding.ActivityMainBinding;
import com.bluewhaleyt.codewhale.tools.editor.basic.SchemeMaterialPalenight;
import com.bluewhaleyt.codewhale.tools.editor.completion.EditorCompletionItemAdapter;
import com.bluewhaleyt.codewhale.tools.editor.completion.EditorCompletionLayout;
import com.bluewhaleyt.codewhale.utils.Constants;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;

import io.github.rosemoe.sora.event.SelectionChangeEvent;
import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;
import io.github.rosemoe.sora.widget.component.Magnifier;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import io.github.rosemoe.sora.widget.schemes.SchemeEclipse;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    private EditorUtil editorUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        bindView(binding);

        editorUtil = new EditorUtil(binding.editor);
        editorUtil.setSymbolInputView(binding.symbolInputView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialize();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            MenuBuilder builder = (MenuBuilder) menu;
            builder.setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                IntentUtil.intent(this, SettingsActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {

        editorUtil = new EditorUtil(binding.editor);
        editorUtil.setNonPrintFlag();

        setupEditor();
        setupMoveSelectionEvent();
        setupToolbar();

        if (PreferencesManager.isSyntaxHighlightingEnabled()) {
            if (PreferencesManager.isTextmateEnabled()) setupTextmateHighlight();
            else setupNormalHighlight();
        } else {
            setNoSyntaxHighlighting();
        }

        if (PreferencesManager.isFollowEditorThemeEnabled()) setColorSurfacesFollowEditorTheme();
        else {
            fixColorSurfaces();
            fixColorSurfaces2();
        }

    }

    private void setColorSurfacesFollowEditorTheme() {
        var colorBg = binding.editor.getColorScheme().getColor(EditorColorScheme.WHOLE_BACKGROUND);
        var colorBgHc = binding.editor.getColorScheme().getColor(EditorColorScheme.CURRENT_LINE);
        var colorText = binding.editor.getColorScheme().getColor(EditorColorScheme.TEXT_NORMAL);

        if (PreferencesManager.isSymbolInputEnabled() || PreferencesManager.isSelectionActionEnabled()) {
            getWindow().setNavigationBarColor(colorBgHc);
        } else {
            getWindow().setNavigationBarColor(colorBg);
        }

        getWindow().setStatusBarColor(colorBg);

        // set toolbar bg color and title color according to the editor themes
        var text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(colorText), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorBg));

        // set symbol input view bg color according to the editor theme
        binding.symbolInputView.setBackgroundColor(colorBgHc);
        binding.symbolInputView.setTextColor(colorText);

        binding.layoutMoveSelection.setBackgroundColor(colorBgHc);
        binding.btnLeft.setColorFilter(colorText);
        binding.btnRight.setColorFilter(colorText);
        binding.btnUp.setColorFilter(colorText);
        binding.btnDown.setColorFilter(colorText);
        binding.btnDuplicateLine.setColorFilter(colorText);
    }

    private void setupEditor() {

        var margin = 30;
        var font = Typeface.createFromAsset(getAssets(), Constants.CODE_FONT);
        binding.editor.setTypefaceText(font);
        binding.editor.setTypefaceLineNumber(font);
        binding.editor.setDividerMargin(margin);
        binding.editor.setLineNumberMarginLeft(margin);
        binding.editor.setLineSpacing(2f, 1.5f);

        setupAutoComplete();
        setupMagnifier();
        setEditorText(AssetsFileLoader.getAssetsFileContent(this, "presets/Main.java"));

        binding.editor.setWordwrap(PreferencesManager.isWordWrapEnabled());
        binding.editor.setScalable(PreferencesManager.isPinchZoomEnabled());
        binding.editor.setLineNumberEnabled(PreferencesManager.isLineNumberEnabled());
        binding.editor.setPinLineNumber(PreferencesManager.isPinLineNumberEnabled());
        binding.editor.setTextSize(PreferencesManager.getFontSize());
        binding.editor.setTabWidth(PreferencesManager.getTabSize());
        binding.editor.setLigatureEnabled(PreferencesManager.isFontLigaturesEnabled());

        binding.editor.getProps().useICULibToSelectWords = PreferencesManager.isICULibEnabled();
        binding.editor.getProps().deleteEmptyLineFast = PreferencesManager.isDeleteEmptyLineFastEnabled();

        binding.editor.getComponent(Magnifier.class).setEnabled(PreferencesManager.isMagnifierEnabled());
        setKeyboardSuggestions();

    }

    private void setupNormalHighlight() {
        var scheme = CommonUtil.isInDarkMode(this) ? new SchemeMaterialPalenight() : new SchemeEclipse();
        binding.editor.setColorScheme(scheme);

        Language language;
        if (PreferencesManager.isLanguageJavaEnabled()) {
//            language = new JavaLanguage();
            language = new AndroidJavaLanguage();
        } else {
            language = new EmptyLanguage();
        }
        binding.editor.setEditorLanguage(language);
    }

    private void setupTextmateHighlight() {
        var themeDark = "material_palenight.json";
        var themeLight = "material_lighter.json";
        try {
            SyntaxHighlightUtil highlighter = new SyntaxHighlightUtil();
            String[] themes = {themeDark, themeLight};
            highlighter.setLanguageBase("languages.json");
            highlighter.setLanguageDirectory(Constants.LANGUAGE_DIR);
            highlighter.setThemeDirectory(Constants.THEME_DIR);
            highlighter.setThemes(themes);

            var theme = CommonUtil.isInDarkMode(this) ? themeDark : themeLight;
            highlighter.setTheme(theme);

            highlighter.setup(this, binding.editor, "test.java");

            CustomSyntaxHighlighter customHighlighter = new CustomSyntaxHighlighter();
            customHighlighter.applyLanguages(binding.editor);

        } catch (Exception e) {
            SnackbarUtil.makeErrorSnackbar(this, e.getMessage(), e.toString());
        }
    }

    private void setNoSyntaxHighlighting() {
        var scheme = CommonUtil.isInDarkMode(this) ? new SchemeMaterialPalenight() : new SchemeEclipse();
        binding.editor.setColorScheme(scheme);
        binding.editor.setEditorLanguage(new EmptyLanguage());
    }

    private void setKeyboardSuggestions() {
        if (PreferencesManager.isKeyboardSuggestionsEnabled()) {
            binding.editor.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            binding.editor.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
    }

    private void setEditorText(String text) {
        editorUtil.setText(text);
    }

    public void fixColorSurfaces2() {
        binding.symbolInputView.setBackgroundColor(CommonUtil.SURFACE_FOLLOW_WINDOW_BACKGROUND);
    }

    private void setupAutoComplete() {
        if (PreferencesManager.isAutoCompletionFollowCursorEnabled()) {
            binding.editor.setCompletionWndPositionMode(CodeEditor.WINDOW_POS_MODE_FOLLOW_CURSOR_ALWAYS);
        } else {
            binding.editor.setCompletionWndPositionMode(0);
        }

        binding.editor.getComponent(EditorAutoCompletion.class).setLayout(new EditorCompletionLayout());
        binding.editor.getComponent(EditorAutoCompletion.class).setAdapter(new EditorCompletionItemAdapter());
        binding.editor.getComponent(EditorAutoCompletion.class).setEnabledAnimation(true);
        binding.editor.getComponent(EditorAutoCompletion.class).setEnabled(PreferencesManager.isAutoCompletionEnabled());
        binding.editor.subscribeEvent(SelectionChangeEvent.class, (event, unsubscribe) -> {
            var editorAutoCompletion = binding.editor.getComponent(EditorAutoCompletion.class);
            CommonUtil.waitForTimeThenDo(50, () -> {
                if (editorAutoCompletion.getCurrentPosition() <= -1) {
                    editorAutoCompletion.moveDown();
                }
                return null;
            });
        });
    }

    private void setupMoveSelectionEvent() {
        var editor = binding.editor;
        binding.btnLeft.setOnClickListener(v -> editor.moveSelectionLeft());
        binding.btnRight.setOnClickListener(v -> editor.moveSelectionRight());
        binding.btnUp.setOnClickListener(v -> editor.moveSelectionUp());
        binding.btnDown.setOnClickListener(v -> editor.moveSelectionDown());
        binding.btnDuplicateLine.setOnClickListener(v -> editor.duplicateLine());
    }

    private void setupToolbar() {
        if (PreferencesManager.isSymbolInputEnabled()) {
            binding.hscrollSymbolView.setVisibility(View.VISIBLE);
        } else {
            binding.hscrollSymbolView.setVisibility(View.GONE);
        }
        if (PreferencesManager.isSelectionActionEnabled()) {
            binding.layoutMoveSelection.setVisibility(View.VISIBLE);
        } else {
            binding.layoutMoveSelection.setVisibility(View.GONE);
        }
    }

    private void setupMagnifier() {
        var scaleSaved = Float.parseFloat(PreferencesManager.getMagnifierScale());
        var scale = 1.25f;
        if (scaleSaved <= 1) {
            binding.editor.getComponent(Magnifier.class).setScaleFactor(scale);
            SnackbarUtil.makeErrorSnackbar(this, getString(R.string.magnifier_resolve, "<=1"));
        } else {
            binding.editor.getComponent(Magnifier.class).setScaleFactor(scaleSaved);
        }
    }

}