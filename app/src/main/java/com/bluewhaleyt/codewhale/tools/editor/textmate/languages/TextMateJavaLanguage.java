package com.bluewhaleyt.codewhale.tools.editor.textmate.languages;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.bluewhaleyt.codewhale.tools.editor.basic.JavaLanguage;

import org.eclipse.tm4e.core.grammar.IGrammar;
import org.eclipse.tm4e.languageconfiguration.model.LanguageConfiguration;

import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete;
import io.github.rosemoe.sora.lang.completion.SimpleSnippetCompletionItem;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;

public class TextMateJavaLanguage extends TextMateLanguage {

    protected TextMateJavaLanguage(IGrammar grammar, LanguageConfiguration languageConfiguration, GrammarRegistry grammarRegistry, ThemeRegistry themeRegistry, boolean createIdentifiers) {
        super(grammar, languageConfiguration, grammarRegistry, themeRegistry, createIdentifiers);
    }

    @Override
    public void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position, @NonNull CompletionPublisher publisher, @NonNull Bundle extraArguments) {
        JavaLanguage.setupAutoComplete(content, position, publisher, extraArguments);
    }

}
