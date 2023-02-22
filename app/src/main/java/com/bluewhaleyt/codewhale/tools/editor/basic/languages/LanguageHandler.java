package com.bluewhaleyt.codewhale.tools.editor.basic.languages;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.WhaleApplication;
import com.bluewhaleyt.codewhale.utils.AssetsFileLoader;

import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete;
import io.github.rosemoe.sora.lang.completion.SimpleSnippetCompletionItem;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet;
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser;
import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;

public class LanguageHandler extends JavaLanguage {

    public static Context context = WhaleApplication.getContext();
    public static IdentifierAutoComplete autoComplete;
    public static String prefix;

    public static String dir = "snippets/";

    public LanguageHandler() {

    }

    @Override
    public void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position,
                                    @NonNull CompletionPublisher publisher, @NonNull Bundle extraArguments) {
        setupAutoComplete(content, position, publisher, extraArguments);
    }

    public void setupAutoComplete(ContentReference content, CharPosition position, CompletionPublisher publisher, Bundle extraArgument) {
        prefix = CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart);
        autoComplete.requireAutoComplete(content,position,prefix, publisher, new IdentifierAutoComplete.SyncIdentifiers());
    }

    public static void addItem(String label, String desc, CodeSnippet snippet, CompletionPublisher publisher) {
        add(label, desc, snippet, publisher, "S");
    }

    public static void addMethodItem(String label, String desc, CodeSnippet snippet, CompletionPublisher publisher) {
        add(label, desc, snippet, publisher, "M");
    }

    private static void add(String label, String desc, CodeSnippet snippet, CompletionPublisher publisher, String str) {
        if (label.toLowerCase().startsWith(prefix) && prefix.length() > 0) {
            publisher.addItem(new SimpleSnippetCompletionItem(label, str + " - " + desc, new SnippetDescription(prefix.length(), snippet, true)));
        }
    }

    public static CodeSnippet parse(String file) {
        return CodeSnippetParser.parse(AssetsFileLoader.getAssetsFileContent(context, dir + file));
    }

    public static String getString(int resId) {
        return context.getString(resId);
    }

}
