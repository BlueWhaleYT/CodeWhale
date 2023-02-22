package com.bluewhaleyt.codewhale.tools.editor.basic.languages;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.bluewhaleyt.codewhale.R;

import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete;
import io.github.rosemoe.sora.lang.completion.SimpleCompletionItem;
import io.github.rosemoe.sora.lang.completion.SimpleSnippetCompletionItem;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet;
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;

public class JavaLanguage extends LanguageHandler {

    public JavaLanguage() {
        autoComplete = new IdentifierAutoComplete(new String[]{
                // unused
                "const", "goto", "strictfp",

                // normal
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
                "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally",
                "float", "for", "if", "implements", "import", "instanceof", "int", "interface", "long",
                "native", "new", "package", "private", "protected", "public", "return", "short", "static",
                "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
                "volatile", "while",

                // contextual
                "exports", "module", "non-sealed", "open", "opens", "permits", "provides", "record",
                "requires", "sealed", "to", "transitive", "uses", "var", "with", "yield",

                // literal values
                "true", "false", "null"
        });
    }

    @Override
    public void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position, @NonNull CompletionPublisher publisher, @NonNull Bundle extraArguments) {
        setupAutoComplete(content, position, publisher, extraArguments);
    }

    public static void setupAutoComplete(ContentReference content, CharPosition position, CompletionPublisher publisher, Bundle extraArgument) {
        LanguageHandler.setup(content, position, publisher, extraArgument);
        setupJavaBasics(publisher);
        setupJavaMethods(publisher);
    }

    public static void setupJavaBasics(CompletionPublisher publisher) {
        addItem("if", "If", parseFromAssets("java/IfStatement.java"), publisher);
        addItem("ifelse", "If Else", parseFromAssets("java/IfElseStatement.java"), publisher);
        addItem("for", "For loop", parseFromAssets("java/ForLoop.java"), publisher);
        addItem("try", "Try Catch", parseFromAssets("java/TryCatch.java"), publisher);
    }

    public static void setupJavaMethods(CompletionPublisher publisher) {
        addMethodItem("main", "main() " + getString(R.string.method_declare), parseFromAssets("java/Main.java"), publisher);
    }

}
