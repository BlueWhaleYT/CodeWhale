package com.bluewhaleyt.codewhale.tools.editor.basic;

import android.content.Context;

import com.bluewhaleyt.codeeditor.textmate.syntaxhighlight.SyntaxHighlightUtil;
import com.bluewhaleyt.codewhale.utils.Constants;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.common.CommonUtil;

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

    public static void setTheme(Context context, CodeEditor editor, String theme, int themeType, String path) {
        switch (themeType) {
            case THEME_NORMAL:
                setNormalTheme(context, editor, theme);
                break;
            case THEME_TEXTMATE:
                try {
                    setTextMateTheme(context, editor, theme, path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private static void setNormalTheme(Context context, CodeEditor editor, String theme) {
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
                colorScheme = CommonUtil.isInDarkMode(context) ? new SchemeDarcula() : new EditorColorScheme();
        }
        editor.setColorScheme(colorScheme);
    }

    private static void setTextMateTheme(Context context, CodeEditor editor, String theme, String path) throws Exception {
        var quietlight = "quietlight.json";
        var darcula = "darcula.json";
        var abyss = "abyss-color-theme.json";
        var tokyoNight = "tokyo-night-theme.json";
        var solarizedDark = "solarized_dark.json";
        var materialDefault = "material_default.json";
        var materialLighter = "material_lighter.json";
        var materialPalenight = "material_palenight.json";
        switch (theme) {
            case "QuietLight":
                theme = quietlight;
                break;
            case "Darcula":
                theme = darcula;
                break;
            case "Abyss":
                theme = abyss;
                break;
            case "Tokyo Night":
                theme = tokyoNight;
                break;
            case "Solarized Dark":
                theme = solarizedDark;
                break;
            case "Material Default":
                theme = materialDefault;
                break;
            case "Material Lighter":
                theme = materialLighter;
                break;
            case "Material Palenight":
                theme = materialPalenight;
                break;
            default:
                theme = CommonUtil.isInDarkMode(context) ? darcula : quietlight;
        }

        SyntaxHighlightUtil highlighter = new SyntaxHighlightUtil();
        String[] themesAva = {
                quietlight, darcula, abyss, tokyoNight, solarizedDark,
                materialDefault, materialLighter, materialPalenight
        };
        highlighter.setLanguageBase("languages.json");
        highlighter.setLanguageDirectory(Constants.LANGUAGE_DIR);
        highlighter.setThemeDirectory(Constants.THEME_DIR);
        highlighter.setThemes(themesAva);
        highlighter.setTheme(theme);
        highlighter.setup(context, editor, path);
    }

}
