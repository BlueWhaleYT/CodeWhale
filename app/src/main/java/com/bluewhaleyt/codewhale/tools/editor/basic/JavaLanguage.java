package com.bluewhaleyt.codewhale.tools.editor.basic;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.bluewhaleyt.codewhale.WhaleApplication;
import com.bluewhaleyt.codewhale.utils.AssetsFileLoader;

import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete;
import io.github.rosemoe.sora.lang.completion.SimpleSnippetCompletionItem;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet;
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;

public class JavaLanguage extends io.github.rosemoe.sora.langs.java.JavaLanguage {

    private static Context context = WhaleApplication.getContext();
    private static String dir = "snippets/java/";

    private static IdentifierAutoComplete autoComplete;

    private static String prefix;

    public final static CodeSnippet IF_STATEMENT = parse("IfStatement.java");
    public final static CodeSnippet IF_ELSE_STATEMENT = parse("IfElseStatement.java");
    public final static CodeSnippet FOR_LOOP = parse("ForLoop.java");
    public final static CodeSnippet TRY_CATCH = parse("TryCatch.java");

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

    public static void setupAutoComplete(ContentReference content, CharPosition position, CompletionPublisher publisher, Bundle extraArgument) {
        prefix = CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart);
        autoComplete.requireAutoComplete(content,position,prefix, publisher, new IdentifierAutoComplete.SyncIdentifiers());

        addItem("if", "If", IF_STATEMENT, publisher);
        addItem("ifelse", "If Else", IF_ELSE_STATEMENT, publisher);
        addItem("for", "For loop", FOR_LOOP, publisher);
        addItem("try", "Try Catch", TRY_CATCH, publisher);
    }

    private static void addItem(String label, String desc, CodeSnippet snippet, CompletionPublisher publisher) {
        if (label.startsWith(prefix) && prefix.length() > 0) {
            publisher.addItem(new SimpleSnippetCompletionItem(label, "S - " + desc, new SnippetDescription(prefix.length(), snippet, true)));
        }
    }

    private static CodeSnippet parse(String file) {
        return CodeSnippetParser.parse(AssetsFileLoader.getAssetsFileContent(context, dir + file));
    }

}
