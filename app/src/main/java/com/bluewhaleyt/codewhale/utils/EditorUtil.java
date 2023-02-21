package com.bluewhaleyt.codewhale.utils;

import com.bluewhaleyt.codewhale.tools.PrettyPrint;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.SymbolInputView;

public class EditorUtil {

    private CodeEditor editor;

    public EditorUtil(CodeEditor editor) {
        this.editor = editor;
    }

    public void setText(String text) {
        if (PreferencesManager.isReplaceTabEnabled()) {
            editor.setText(text.replace("\t", " "));
        } else {
            editor.setText(PrettyPrint.byBracket(text).toString());
        }
    }

    public void setSymbolInputView(SymbolInputView symbolInputView) {
        String[] display = {"→", "{", "}", "(", ")", "[", "]", "<", ">", ",", ".", ";", "\"", "'", "?", "+", "-", "*", "/", "="};
        String[] insertText = {"\t", "{}", "}", "()", ")", "[]", "]", "<", ">", ",", ".", ";", "\"", "'", "?", "+", "-", "*", "/", "="};
        symbolInputView.addSymbols(display, insertText);
        symbolInputView.bindEditor(editor);
    }
    
    public void setNonPrintFlag() {
        int npf1 = 0, npf2 = 0, npf3 = 0, npf4 = 0, npf5 = 0, npf6 = 0, npf7 = 0;
        if (PreferencesManager.isNonPrintFlagEnabled()) {
            if (PreferencesManager.isNPFWSInnerEnabled()) npf1 = CodeEditor.FLAG_DRAW_WHITESPACE_INNER;
            else if (PreferencesManager.isNPFWSLeadingEnabled()) npf2 = CodeEditor.FLAG_DRAW_WHITESPACE_LEADING;
            else if (PreferencesManager.isNPFWSTrailingEnabled()) npf3 = CodeEditor.FLAG_DRAW_WHITESPACE_TRAILING;
            else if (PreferencesManager.isNPFWSForEmptyLineEnabled()) npf4 = CodeEditor.FLAG_DRAW_WHITESPACE_FOR_EMPTY_LINE;
            else if (PreferencesManager.isNPFWSInSelectionEnabled()) npf5 = CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION;
            else if (PreferencesManager.isNPFTabSameAsSpaceEnabled()) npf6 = CodeEditor.FLAG_DRAW_TAB_SAME_AS_SPACE;
            else if (PreferencesManager.isNPFLineSeparatorEnabled()) npf7 = CodeEditor.FLAG_DRAW_LINE_SEPARATOR;
            editor.setNonPrintablePaintingFlags(npf1 | npf2 | npf3 | npf4 | npf5 | npf6 | npf7);
        }
    }

}
