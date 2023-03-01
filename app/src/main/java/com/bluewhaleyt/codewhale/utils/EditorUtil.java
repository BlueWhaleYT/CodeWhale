package com.bluewhaleyt.codewhale.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.tools.PrettyPrint;
import com.bluewhaleyt.common.DynamicColorsUtil;

import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.DirectAccessProps;
import io.github.rosemoe.sora.widget.SymbolInputView;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;
import io.github.rosemoe.sora.widget.component.Magnifier;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import io.github.rosemoe.sora.widget.style.LineInfoPanelPosition;
import io.github.rosemoe.sora.widget.style.LineInfoPanelPositionMode;
import io.github.rosemoe.sora.widget.style.LineNumberTipTextProvider;

public class EditorUtil {

    private Context context;
    private CodeEditor editor;
    private EditorColorScheme colorScheme;

    public EditorUtil(Context context, CodeEditor editor) {
        this.context = context;
        this.editor = editor;
    }

    public EditorUtil(Context context, CodeEditor editor, EditorColorScheme colorScheme) {
        this.context = context;
        this.editor = editor;
        this.colorScheme = colorScheme;
    }

    public void setup() {
        var margin = PreferencesManager.getLineNumberMargin();
        var font = Typeface.createFromAsset(context.getAssets(), Constants.CODE_FONT);
        editor.setTypefaceText(font);
        editor.setTypefaceLineNumber(font);
        editor.setDividerMargin(margin);
        editor.setLineNumberMarginLeft(margin);
        editor.setLineSpacing(2f, 1.5f);

        editor.setWordwrap(PreferencesManager.isWordWrapEnabled());
        editor.setScalable(PreferencesManager.isPinchZoomEnabled());
        editor.setLineNumberEnabled(PreferencesManager.isLineNumberEnabled());
        editor.setPinLineNumber(PreferencesManager.isPinLineNumberEnabled());
        editor.setTextSize(PreferencesManager.getFontSize());
        editor.setTabWidth(PreferencesManager.getTabSize());
        editor.setLigatureEnabled(PreferencesManager.isFontLigaturesEnabled());
        editor.setHighlightBracketPair(PreferencesManager.isHighlightBracketDelimiterEnabled());

        editor.getProps().drawSideBlockLine = false;
        editor.getProps().useICULibToSelectWords = PreferencesManager.isICULibEnabled();
        editor.getProps().deleteEmptyLineFast = PreferencesManager.isDeleteEmptyLineFastEnabled();
        editor.getProps().autoIndent = PreferencesManager.isAutoIndentEnabled();
        editor.getProps().disallowSuggestions = PreferencesManager.isKeyboardSuggestionsEnabled();
        editor.getProps().actionWhenLineNumberClicked = PreferencesManager.isLineNumberClickSelectEnabled() ? DirectAccessProps.LN_ACTION_SELECT_LINE : DirectAccessProps.LN_ACTION_PLACE_SELECTION_HOME;

        editor.getComponent(Magnifier.class).setEnabled(PreferencesManager.isMagnifierEnabled());

        setLineNumberAlign();
        setTextActionWindow(context, colorScheme);

        var gd = new GradientDrawable();
        var color = new DynamicColorsUtil(context).getColorPrimary();
        gd.setCornerRadius(8);
        gd.setColor(ColorUtils.setAlphaComponent(color, 50));
        editor.setHorizontalScrollbarThumbDrawable(gd);
        editor.setVerticalScrollbarThumbDrawable(gd);

        editor.setLnPanelPositionMode(LineInfoPanelPositionMode.FIXED);
        editor.setLnPanelPosition(LineInfoPanelPosition.BOTTOM | LineInfoPanelPosition.RIGHT);
        editor.setLineInfoTextSize(24);
        editor.setLineNumberTipTextProvider(codeEditor -> context.getString(R.string.current_first_visible_line) + ": " + (editor.getFirstVisibleLine() + 1));
    }

    public void undo() {
        if (editor.canUndo()) editor.undo();
    }

    public void redo() {
        if (editor.canRedo()) editor.redo();
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
    
    public void setLineNumberAlign() {
        Paint.Align align = null;
        var alignVal = PreferencesManager.getLineNumberAlign();
        switch (alignVal) {
            case "left":
                align = Paint.Align.LEFT;
                break;
            case "right":
                align = Paint.Align.RIGHT;
                break;
            case "center":
                align = Paint.Align.CENTER;
                break;
        }
        editor.setLineNumberAlign(align);
    }

    public void setTextActionWindow(Context context, EditorColorScheme colorScheme) {
        var colorBg = colorScheme.getColor(EditorColorScheme.WHOLE_BACKGROUND);
        var colorText = colorScheme.getColor(EditorColorScheme.TEXT_NORMAL);

        View v = LayoutInflater.from(context).inflate(io.github.rosemoe.sora.R.layout.text_compose_panel, null);

        var root = v.findViewById(io.github.rosemoe.sora.R.id.panel_root);
        ImageButton btnSelectAll = v.findViewById(io.github.rosemoe.sora.R.id.panel_btn_select_all);
        ImageButton btnCopy = v.findViewById(io.github.rosemoe.sora.R.id.panel_btn_copy);
        ImageButton btnPaste = v.findViewById(io.github.rosemoe.sora.R.id.panel_btn_paste);
        ImageButton btnCut = v.findViewById(io.github.rosemoe.sora.R.id.panel_btn_cut);

        root.setBackgroundColor(colorBg);
        btnSelectAll.setColorFilter(colorText);
        btnCopy.setColorFilter(colorText);
        btnPaste.setColorFilter(colorText);
        btnCut.setColorFilter(colorText);
        editor.getComponent(EditorTextActionWindow.class).setContentView(v);

        btnSelectAll.setOnClickListener(view -> editor.selectAll());
        btnCopy.setOnClickListener(view -> editor.copyText());
        btnPaste.setOnClickListener(view -> editor.pasteText());
        btnCut.setOnClickListener(view -> editor.cutText());

    }

    public void setUndoRedoState(MenuItem undo, MenuItem redo) {
        if (undo == null || redo == null) return;
        if (editor.canUndo()) {
            undo.setEnabled(true);
            undo.getIcon().setAlpha(255);
        } else {
            undo.setEnabled(false);
            undo.getIcon().setAlpha(getAlpha());
        }
        if (editor.canRedo()) {
            redo.setEnabled(true);
            redo.getIcon().setAlpha(255);
        } else {
            redo.setEnabled(false);
            redo.getIcon().setAlpha(getAlpha());
        }
    }

    public void disableUndoRedo(MenuItem undo, MenuItem redo) {
        undo.setEnabled(false);
        redo.setEnabled(false);
        undo.getIcon().setAlpha(getAlpha());
        redo.getIcon().setAlpha(getAlpha());
    }

    public int getAlpha() {
        return 130;
    }
}
