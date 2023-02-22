package com.bluewhaleyt.codewhale.tools.editor.basic.languages.modules;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.tools.editor.basic.languages.JavaLanguage;
import com.bluewhaleyt.codewhale.tools.editor.basic.languages.LanguageHandler;

import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete;
import io.github.rosemoe.sora.lang.completion.SimpleCompletionItem;
import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet;
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;

public class AndroidJavaLanguage extends JavaLanguage {

    @Override
    public void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position, @NonNull CompletionPublisher publisher, @NonNull Bundle extraArguments) {
        setupAutoComplete(content, position, publisher, extraArguments);
    }

    public static void setupAutoComplete(ContentReference content, CharPosition position, CompletionPublisher publisher, Bundle extraArgument) {
        LanguageHandler.setup(content, position, publisher, extraArgument);
        JavaLanguage.setupJavaBasics(publisher);
        JavaLanguage.setupJavaMethods(publisher);
        setupAndroidBasics(publisher);
        setupAndroidMethods(publisher);
    }

    public static void setupAndroidMethods(CompletionPublisher publisher) {
        addMethodItem("onCreate()", "onCreate() " + getString(R.string.method_declare), parseFromAssets("android/java/OnCreate.java"), publisher);
        addMethodItem("onStart()", "onStart() " + getString(R.string.method_declare), parseFromAssets("android/java/OnStart.java"), publisher);
        addMethodItem("onResume()", "onResume() " + getString(R.string.method_declare), parseFromAssets("android/java/OnResume.java"), publisher);
        addMethodItem("onPause()", "onPause() " + getString(R.string.method_declare), parseFromAssets("android/java/OnPause.java"), publisher);
        addMethodItem("onStop()", "onStop() " + getString(R.string.method_declare), parseFromAssets("android/java/OnStop.java"), publisher);
        addMethodItem("onDestroy()", "onDestroy() " + getString(R.string.method_declare), parseFromAssets("android/java/OnDestroy.java"), publisher);
        addMethodItem("onRestart()", "onRestart() " + getString(R.string.method_declare), parseFromAssets("android/java/OnRestart.java"), publisher);
    }

    public static void setupAndroidBasics(CompletionPublisher publisher) {
        addItem("Toast", "Create a toast", parse("Toast.makeText(${1:this}, ${2:text}, Toast.LENGTH_SHORT);"), publisher);
    }

}
