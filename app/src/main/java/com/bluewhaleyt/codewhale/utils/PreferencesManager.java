package com.bluewhaleyt.codewhale.utils;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.bluewhaleyt.codewhale.WhaleApplication;

public class PreferencesManager {

    public static boolean isSyntaxHighlightingEnabled() {
        return getPrefs().getBoolean("pref_enable_syntax_highlighting", true);
    }

    public static boolean isTextmateEnabled() {
        return getPrefs().getBoolean("pref_enable_textmate", true);
    }

    public static boolean isLanguageJavaEnabled() {
        return getPrefs().getBoolean("pref_enable_language_java", true);
    }

    public static boolean isFollowEditorThemeEnabled() {
        return getPrefs().getBoolean("pref_enable_follow_editor_theme", true);
    }

    public static boolean isWordWrapEnabled() {
        return getPrefs().getBoolean("pref_enable_word_wrap", false);
    }

    public static boolean isAutoCompletionEnabled() {
        return getPrefs().getBoolean("pref_enable_auto_completion", true);
    }

    public static boolean isPinchZoomEnabled() {
        return getPrefs().getBoolean("pref_enable_pinch_zoom", true);
    }

    public static boolean isMagnifierEnabled() {
        return getPrefs().getBoolean("pref_enable_magnifier", true);
    }

    public static boolean isICULibEnabled() {
        return getPrefs().getBoolean("pref_enable_icu_lib", true);
    }

    public static boolean isLineNumberEnabled() {
        return getPrefs().getBoolean("pref_enable_line_number", true);
    }

    public static boolean isPinLineNumberEnabled() {
        return getPrefs().getBoolean("pref_enable_pin_line_number", true);
    }

    public static boolean isDeleteEmptyLineFastEnabled() {
        return getPrefs().getBoolean("pref_enable_delete_empty_line_fast", false);
    }

    public static boolean isKeyboardSuggestionsEnabled() {
        return getPrefs().getBoolean("pref_enable_keyboard_suggestions", false);
    }

    public static boolean isFontLigaturesEnabled() {
        return getPrefs().getBoolean("pref_enable_font_ligatures", false);
    }

    public static boolean isDynamicColorEnabled() {
        return getPrefs().getBoolean("pref_enable_dynamic_color", false);
    }

    public static boolean isReplaceTabEnabled() {
        return getPrefs().getBoolean("pref_enable_replace_tab", true);
    }

    public static boolean isNonPrintFlagEnabled() {
        return getPrefs().getBoolean("pref_enable_non_print_flag", true);
    }

    public static boolean isNPFWSInnerEnabled() {
        return getPrefs().getBoolean("pref_enable_npf_ws_inner", false);
    }

    public static boolean isNPFWSLeadingEnabled() {
        return getPrefs().getBoolean("pref_enable_npf_ws_leading", false);
    }

    public static boolean isNPFWSTrailingEnabled() {
        return getPrefs().getBoolean("pref_enable_npf_ws_trailing", false);
    }

    public static boolean isNPFWSForEmptyLineEnabled() {
        return getPrefs().getBoolean("pref_enable_npf_ws_for_empty_line", false);
    }

    public static boolean isNPFWSInSelectionEnabled() {
        return getPrefs().getBoolean("pref_enable_npf_ws_in_selection", true);
    }

    public static boolean isNPFTabSameAsSpaceEnabled() {
        return getPrefs().getBoolean("pref_enable_npf_tab_same_as_space", false);
    }

    public static boolean isNPFLineSeparatorEnabled() {
        return getPrefs().getBoolean("pref_enable_npf_line_separator", false);
    }

    public static boolean isAutoCompletionFollowCursor() {
        return getPrefs().getBoolean("pref_enable_auto_completion_follow_cursor", false);
    }

    public static int getFontSize() {
        return getPrefs().getInt("pref_font_size", 18);
    }

    public static int getTabSize() {
        return getPrefs().getInt("pref_tab_size", 4);
    }

    public static String getAppTheme() {
        return getPrefs().getString("pref_app_theme", "auto");
    }

    public static SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(WhaleApplication.getContext());
    }

}