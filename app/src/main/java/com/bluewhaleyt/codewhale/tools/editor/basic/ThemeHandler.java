package com.bluewhaleyt.codewhale.tools.editor.basic;

import android.content.Context;

import com.bluewhaleyt.codeeditor.textmate.syntaxhighlight.SyntaxHighlightUtil;
import com.bluewhaleyt.codewhale.utils.Constants;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula;
import io.github.rosemoe.sora.widget.schemes.SchemeEclipse;
import io.github.rosemoe.sora.widget.schemes.SchemeGitHub;
import io.github.rosemoe.sora.widget.schemes.SchemeNotepadXX;
import io.github.rosemoe.sora.widget.schemes.SchemeVS2019;

public class ThemeHandler {

    public static final int THEME_NORMAL = 0;
    public static final int THEME_TEXTMATE = 1;

    private static EditorColorScheme colorScheme;

    private static SyntaxHighlightUtil syntaxHighlightUtil;

    private static String theme;

    public static void setTheme(Context context, CodeEditor editor, String theme, int themeType) {
        switch (themeType) {
            case THEME_NORMAL:
                setNormalTheme(editor, theme);
                break;
            case THEME_TEXTMATE:
                try {
                    setTextMateTheme(context, editor, theme);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private static void setNormalTheme(CodeEditor editor, String theme) {
        switch (theme) {
            case "Github":
                colorScheme = new SchemeGitHub();
                break;
            case "Eclipse":
                colorScheme = new SchemeEclipse();
                break;
            case "NotepadXX":
                colorScheme = new SchemeNotepadXX();
                break;
            case "Darcula":
                colorScheme = new SchemeDarcula();
                break;
            case "VS2019":
                colorScheme = new SchemeVS2019();
                break;
            case "Material Palenight":
                colorScheme = new SchemeMaterialPalenight();
                break;
            case "Default":
            default:
                colorScheme = new EditorColorScheme();
        }
        editor.setColorScheme(colorScheme);
    }

    private static void setTextMateTheme(Context context, CodeEditor editor, String theme) throws Exception {
        var materialLighter = "material_lighter.json";
        var materialPalenight = "material_palenight.json";
        switch (theme) {
            case "Material Lighter":
                theme = materialLighter;
                break;
            case "Material Palenight":
                theme = materialPalenight;
                break;
        }

        SyntaxHighlightUtil highlighter = new SyntaxHighlightUtil();
        String[] themesAva = {materialLighter, materialPalenight};
        highlighter.setLanguageBase("languages.json");
        highlighter.setLanguageDirectory(Constants.LANGUAGE_DIR);
        highlighter.setThemeDirectory(Constants.THEME_DIR);
        highlighter.setThemes(themesAva);
        highlighter.setTheme(theme);
        highlighter.setup(context, editor, "test.java");
    }

}
