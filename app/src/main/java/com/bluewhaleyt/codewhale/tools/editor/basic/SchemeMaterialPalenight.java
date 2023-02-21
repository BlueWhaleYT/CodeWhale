package com.bluewhaleyt.codewhale.tools.editor.basic;

import android.graphics.Color;

import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class SchemeMaterialPalenight extends EditorColorScheme {

    public static int purple = Color.parseColor("#c792ea");
    public static int lightBlue = Color.parseColor("#89ddff");
    public static int blue = Color.parseColor("#82aaff");
    public static int lime = Color.parseColor("#c3e88d");
    public static int red = Color.parseColor("#ff5874");
    public static int orange = Color.parseColor("#f78c6c");
    public static int yellow = Color.parseColor("#ffcb6b");
    public static int cyan = Color.parseColor("#80cbc4");
    public static int lightGrey = Color.parseColor("#687098");
    public static int grey = Color.parseColor("#4c5374");
    public static int black = Color.parseColor("#292d3e");
    public static int darkBlack = Color.parseColor("#1b1f2b");

    public static int normal = Color.parseColor("#a6accd");

    public static int colorAccent = Color.parseColor("#444aff");

    @Override
    public void applyDefault(){
        super.applyDefault();

        // background color
        setColor(WHOLE_BACKGROUND, black);

        // text color
        setColor(TEXT_NORMAL, normal);

        // line number background
        setColor(LINE_NUMBER_BACKGROUND, black);
        setColor(LINE_DIVIDER, Color.TRANSPARENT);
        setColor(LINE_NUMBER_PANEL, black);

        // line number text color
        setColor(LINE_NUMBER, grey);
        setColor(LINE_NUMBER_PANEL_TEXT, grey);

        // non printable flags color
        setColor(NON_PRINTABLE_CHAR, grey);

        // current line background color
        setColor(CURRENT_LINE, darkBlack);
        setColor(BLOCK_LINE_CURRENT, lightGrey);
        setColor(BLOCK_LINE, grey);

        // selected text background color
        setColor(SELECTED_TEXT_BACKGROUND, darkBlack);

        // selections
        setColor(SELECTION_HANDLE, colorAccent);
        setColor(SELECTION_INSERT, colorAccent);

        // scrollbars
        setColor(SCROLL_BAR_THUMB, darkBlack);

        // auto complete menu
        setColor(COMPLETION_WND_BACKGROUND, darkBlack);
        setColor(COMPLETION_WND_TEXT_PRIMARY, normal);
        setColor(COMPLETION_WND_TEXT_SECONDARY, grey);
        setColor(COMPLETION_WND_CORNER, darkBlack);

        // bracket pair highlights
        setColor(HIGHLIGHTED_DELIMITERS_FOREGROUND, yellow);

        /* Java - Syntax Highlights */

        // keywords
        setColor(KEYWORD, purple);

        // identifiers
        setColor(IDENTIFIER_NAME, normal);
        setColor(IDENTIFIER_VAR, blue);

        // functions
        setColor(FUNCTION_NAME, blue);

        // annotations
        setColor(ANNOTATION, lightBlue);

        // literals
        setColor(LITERAL, lime);

        // operators
        setColor(OPERATOR, lightBlue);

        // comments
        setColor(COMMENT, grey);

        /* HTML - Syntax Highlights */

        setColor(HTML_TAG, red);
        setColor(ATTRIBUTE_NAME, purple);
        setColor(ATTRIBUTE_VALUE, lime);


    }

}
