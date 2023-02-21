package com.bluewhaleyt.codewhale.tools.editor.textmate;

import com.bluewhaleyt.codeeditor.textmate.syntaxhighlight.SyntaxHighlightUtil;
import com.bluewhaleyt.codewhale.tools.editor.textmate.languages.TextMateJavaLanguage;

import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.widget.CodeEditor;

public class CustomSyntaxHighlighter extends SyntaxHighlightUtil {

    private String lang = "source.java";

    public void ensureTextmateTheme(CodeEditor editor) throws Exception {
        var editorColorScheme = editor.getColorScheme();
        if (!(editorColorScheme instanceof CustomTextMateColorScheme)) {
            editorColorScheme = CustomTextMateColorScheme.create(ThemeRegistry.getInstance());
            editor.setColorScheme(editorColorScheme);
        }
    }

    public void applyLanguages(CodeEditor editor) throws Exception {
        ensureTextmateTheme(editor);
//        TextMateLanguage language;
//        var editorLanguage = editor.getEditorLanguage();
//        if (editorLanguage instanceof TextMateLanguage) {
//            language = (TextMateLanguage) editorLanguage;
//            language.updateLanguage(lang);
//        } else {
//            language = TextMateJavaLanguage.create(lang, true);
//        }
//        editor.setEditorLanguage(language);
    }

}
