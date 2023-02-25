package com.bluewhaleyt.codewhale.tools.editor.basic;

import android.app.Activity;
import android.content.Context;

import com.bluewhaleyt.codeeditor.textmate.syntaxhighlight.SyntaxHighlightUtil;
import com.bluewhaleyt.codewhale.utils.Constants;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.common.CommonUtil;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;

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
                    SnackbarUtil.makeErrorSnackbar((Activity) context, e.getMessage(), e.toString());
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
        switch (theme) {
            case "QuietLight":
                theme = "quietlight.json";
                break;
            case "Darcula":
                theme = "darcula.json";
                break;
            case "Abyss":
                theme = "abyss-color-theme.json";
                break;
            case "Tokyo Night":
                theme = "tokyo-night-theme.json";
                break;
            case "Solarized Light":
                theme = "solarized_light.json";
                break;
            case "Solarized Dark":
                theme = "solarized_dark.json";
                break;
            case "Material Default":
                theme = "material_default.json";
                break;
            case "Material Lighter":
                theme = "material_lighter.json";
                break;
            case "Material Palenight":
                theme = "material_palenight.json";
                break;
            case "One Light":
                theme = "OneLight.json";
                break;
            case "One Dark":
                theme = "OneDark.json";
                break;
            case "One Dark Pro":
                theme = "OneDark-Pro.json";
                break;
            case "One Dark Pro Darker":
                theme = "OneDark-Pro-darker.json";
                break;
            case "Monokai":
                theme = "monokai-color-theme.json";
                break;
            case "Mirage":
                theme = "Mirage-color-theme.json";
                break;
            case "Light Owl":
                theme = "light-owl-color-theme.json";
                break;
            case "Night Owl":
                theme = "night-owl-color-theme.json";
                break;
            case "Palenight":
                theme = "palenight.json";
                break;
            default:
                theme = CommonUtil.isInDarkMode(context) ? Constants.DEFAULT_DARK_THEME : Constants.DEFAULT_LIGHT_THEME;
        }

        SyntaxHighlightUtil highlighter = new SyntaxHighlightUtil();
        highlighter.setLanguageBase("languages.json");
        highlighter.setLanguageDirectory(Constants.LANGUAGE_DIR);
        highlighter.setThemeDirectory(Constants.THEME_DIR);
        highlighter.setThemes(new String[]{theme});
        highlighter.setTheme(theme);
        highlighter.setup(context, editor, path);
    }

}
