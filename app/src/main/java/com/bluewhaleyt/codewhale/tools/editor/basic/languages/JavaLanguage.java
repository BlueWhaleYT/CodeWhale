package com.bluewhaleyt.codewhale.tools.editor.basic.languages;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.bluewhaleyt.codewhale.R;

import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete;
import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;

public class JavaLanguage extends LanguageHandler {

    public final static CodeSnippet MAIN_METHOD = parse("java/Main.java");
    public final static CodeSnippet IF_STATEMENT = parse("java/IfStatement.java");
    public final static CodeSnippet IF_ELSE_STATEMENT = parse("java/IfElseStatement.java");
    public final static CodeSnippet FOR_LOOP = parse("java/ForLoop.java");
    public final static CodeSnippet TRY_CATCH = parse("java/TryCatch.java");

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
    public void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position,
                                    @NonNull CompletionPublisher publisher, @NonNull Bundle extraArguments) {
        setupAutoComplete(content, position, publisher, extraArguments);
    }

    public void setupAutoComplete(ContentReference content, CharPosition position, CompletionPublisher publisher, Bundle extraArgument) {
        prefix = CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart);
        autoComplete.requireAutoComplete(content,position,prefix, publisher, new IdentifierAutoComplete.SyncIdentifiers());

        setupJavaBasics(publisher);
        setupJavaMethods(publisher);
    }

    public static void setupJavaBasics(CompletionPublisher publisher) {
        addItem("if", "If", IF_STATEMENT, publisher);
        addItem("ifelse", "If Else", IF_ELSE_STATEMENT, publisher);
        addItem("for", "For loop", FOR_LOOP, publisher);
        addItem("try", "Try Catch", TRY_CATCH, publisher);
    }

    public static void setupJavaMethods(CompletionPublisher publisher) {
        addItem("main", "main() " + getString(R.string.method_declare), MAIN_METHOD, publisher);
    }

}
