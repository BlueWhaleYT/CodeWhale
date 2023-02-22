package com.bluewhaleyt.codewhale.tools.editor.basic.languages.modules;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.tools.editor.basic.languages.JavaLanguage;

import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete;
import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;

public class AndroidJavaLanguage extends JavaLanguage {

    public final static CodeSnippet ON_CREATE_METHOD = parse("android/java/OnCreate.java");
    public final static CodeSnippet ON_START_METHOD = parse("android/java/OnStart.java");
    public final static CodeSnippet ON_RESUME_METHOD = parse("android/java/OnResume.java");
    public final static CodeSnippet ON_PAUSE_METHOD = parse("android/java/OnPause.java");
    public final static CodeSnippet ON_STOP_METHOD = parse("android/java/OnStop.java");
    public final static CodeSnippet ON_DESTROY_METHOD = parse("android/java/OnDestroy.java");
    public final static CodeSnippet ON_RESTART_METHOD = parse("android/java/OnRestart.java");

    @Override
    public void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position,
                                    @NonNull CompletionPublisher publisher, @NonNull Bundle extraArguments) {
        setupAutoComplete(content, position, publisher, extraArguments);
    }

    public void setupAutoComplete(ContentReference content, CharPosition position, CompletionPublisher publisher, Bundle extraArgument) {
        prefix = CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart);
        autoComplete.requireAutoComplete(content,position,prefix, publisher, new IdentifierAutoComplete.SyncIdentifiers());

        JavaLanguage.setupJavaBasics(publisher);

        addMethodItem("onCreate()", "onCreate() " + getString(R.string.method_declare), ON_CREATE_METHOD, publisher);
        addMethodItem("onStart()", "onStart() " + getString(R.string.method_declare), ON_START_METHOD, publisher);
        addMethodItem("onResume()", "onResume() " + getString(R.string.method_declare), ON_RESUME_METHOD, publisher);
        addMethodItem("onPause()", "onPause() " + getString(R.string.method_declare), ON_PAUSE_METHOD, publisher);
        addMethodItem("onStop()", "onStop() " + getString(R.string.method_declare), ON_STOP_METHOD, publisher);
        addMethodItem("onDestroy()", "onDestroy() " + getString(R.string.method_declare), ON_DESTROY_METHOD, publisher);
        addMethodItem("onRestart()", "onRestart() " + getString(R.string.method_declare), ON_RESTART_METHOD, publisher);



    }

}
