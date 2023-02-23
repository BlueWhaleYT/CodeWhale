package com.bluewhaleyt.codewhale.tools.editor.completion;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.WhaleApplication;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;

import io.github.rosemoe.sora.widget.component.CompletionLayout;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class EditorCompletionLayout implements CompletionLayout {

    private ListView listView;
    private ProgressBar progressBar;
    private LinearLayout rootView;
    private TextView textView;
    private EditorAutoCompletion editorAutoCompletion;

    private GradientDrawable gd;

    public boolean enabledAnimation = false;

    private Context context;

    @Override
    public void onApplyColorScheme(@NonNull EditorColorScheme colorScheme) {
        gd = new GradientDrawable();
//        gd.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, editorAutoCompletion.getContext().getResources().getDisplayMetrics()))
        gd.setColor(colorScheme.getColor(EditorColorScheme.COMPLETION_WND_BACKGROUND));
        setStroke(colorScheme);
        rootView.setBackground(gd);
        textView.setTextColor(colorScheme.getColor(EditorColorScheme.TEXT_NORMAL));
    }

    @Override
    public void setEditorCompletion(@NonNull EditorAutoCompletion completion) {
        editorAutoCompletion = completion;
    }

    @NonNull
    @Override
    public View inflate(@NonNull Context context) {
        var rootLayout = new LinearLayout(context);
        rootView = rootLayout;
        listView = new ListView(context);
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        textView = new TextView(context);

        rootLayout.setOrientation(LinearLayout.VERTICAL);

        setEnabledAnimation(false);

        rootLayout.addView(progressBar, new LinearLayout.LayoutParams(-1, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics())));
        rootLayout.addView(listView, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        rootLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        textView.setText(context.getString(R.string.auto_completion_hint, "⏎"));
        textView.setTextSize(12);
        textView.setMaxLines(1);

        progressBar.setIndeterminate(true);
        var progressBarLayoutParams = (LinearLayout.LayoutParams) progressBar.getLayoutParams();
        var textViewLayoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();

        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        progressBarLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -12, context.getResources().getDisplayMetrics());
        progressBarLayoutParams.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -8, context.getResources().getDisplayMetrics());
        progressBarLayoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
        progressBarLayoutParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());

        textViewLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
        textViewLayoutParams.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
        textViewLayoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
        textViewLayoutParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()));

        rootLayout.setBackground(gd);

        listView.setDividerHeight(0);
        setLoading(true);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            editorAutoCompletion.select(position);
        });

        return rootLayout;
    }

    @NonNull
    @Override
    public AdapterView<?> getCompletionList() {
        return listView;
    }

    @Override
    public void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void ensureListPositionVisible(int position, int incrementPixels) {
        listView.post(() -> {
            while (listView.getFirstVisiblePosition() + 1 > position && listView.canScrollList(-1)) {
                performScrollList(incrementPixels / 2);
            }
            while (listView.getLastVisiblePosition() - 1 < position && listView.canScrollList(1)) {
                performScrollList(-incrementPixels / 2);
            }
        });
    }

    @Override
    public void setEnabledAnimation(boolean enabledAnimation) {
        this.enabledAnimation = enabledAnimation;

        if (enabledAnimation) {
            rootView.setLayoutTransition(new LayoutTransition());
            var transition = rootView.getLayoutTransition();
            transition.enableTransitionType(LayoutTransition.CHANGING);
            transition.enableTransitionType(LayoutTransition.APPEARING);
            transition.enableTransitionType(LayoutTransition.DISAPPEARING);
            transition.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
            transition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);

//            listView.setLayoutTransition(rootView.getLayoutTransition());
        } else  {
            rootView.setLayoutTransition(null);
//            listView.setLayoutTransition(null);
        }
    }

    public float valueInDp(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    private void performScrollList(int offset) {
        var adpView = getCompletionList();

        long down = SystemClock.uptimeMillis();
        var ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_DOWN, 0, 0, 0);
        adpView.onTouchEvent(ev);
        ev.recycle();

        ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_MOVE, 0, offset, 0);
        adpView.onTouchEvent(ev);
        ev.recycle();

        ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_CANCEL, 0, offset, 0);
        adpView.onTouchEvent(ev);
        ev.recycle();
    }

    private void setStroke(EditorColorScheme colorScheme) {
        if (PreferencesManager.isAutoCompletionStrokeEnabled()) {
            gd.setStroke(6, colorScheme.getColor(EditorColorScheme.COMPLETION_WND_CORNER));
        }
    }

}
