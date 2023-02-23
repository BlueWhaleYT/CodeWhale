package com.bluewhaleyt.codewhale.tools.editor.basic;

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

    public static void setTheme(CodeEditor editor, String theme, int themeType) {
        switch (themeType) {
            case THEME_NORMAL:
                setNormalTheme(theme);
                break;
            case THEME_TEXTMATE:
                setTextMateTheme(theme);
                break;
        }

        editor.setColorScheme(colorScheme);
    }

    private static void setNormalTheme(String theme) {
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
    }

    private static void setTextMateTheme(String theme) {

    }

}
