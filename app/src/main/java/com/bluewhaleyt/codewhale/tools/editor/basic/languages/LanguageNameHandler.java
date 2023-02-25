package com.bluewhaleyt.codewhale.tools.editor.basic.languages;

public class LanguageNameHandler {

    public static String getLanguageCode(String lang) {
        var langCode = "";
        switch (lang) {
            case "CSS":
                langCode = "css";
                break;
            case "HTML":
                langCode = "html";
                break;
            case "Java":
                langCode = "java";
                break;
            case "JavaScript":
                langCode = "js";
                break;
            case "JSON":
                langCode = "json";
                break;
            case "Kotlin":
                langCode = "kt";
                break;
            case "PHP":
                langCode = "php";
                break;
            case "Python":
                langCode = "py";
                break;
        }
        return langCode;
    }

}
