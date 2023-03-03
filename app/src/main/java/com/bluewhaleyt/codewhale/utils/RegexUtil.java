package com.bluewhaleyt.codewhale.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegexUtil {

    private String text, regex;
    private int flags;

    public static final String COLOR_HEX_PATTERN = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$";

    public static final String URL_PATTERN = "\\b((?:https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:, .;]*[-a-zA-Z0-9+&@#/%=~_|])";

    public RegexUtil() {

    }

    public RegexUtil(String text, String regex) {
        this.text = text;
        this.regex = regex;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public List<?> getMatchedText() {
        var list = new ArrayList<>();
        var p = Pattern.compile(regex, flags);
        var m = p.matcher(text);
        while (m.find()) list.add(text.substring(m.start(0), m.end(0)));
        return list;
    }

}
