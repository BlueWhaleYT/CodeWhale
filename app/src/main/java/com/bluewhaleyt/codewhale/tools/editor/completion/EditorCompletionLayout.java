package com.bluewhaleyt.codewhale.tools.editor.completion;

import android.animation.LayoutTransition;
import android.content.Context;
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

import androidx.annotation.NonNull;

import io.github.rosemoe.sora.widget.component.CompletionLayout;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class EditorCompletionLayout implements CompletionLayout {

    private ListView listView;
    private ProgressBar progressBar;
    private RelativeLayout layout;
    private EditorAutoCompletion editorAutoCompletion;

    public boolean enabledAnimation = false;

    private Context context;

    @Override
    public void onApplyColorScheme(@NonNull EditorColorScheme colorScheme) {
        GradientDrawable gd = new GradientDrawable();
//        gd.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, editorAutoCompletion.getContext().getResources().getDisplayMetrics()));
        gd.setStroke(6, colorScheme.getColor(EditorColorScheme.COMPLETION_WND_CORNER));
        gd.setColor(colorScheme.getColor(EditorColorScheme.COMPLETION_WND_BACKGROUND));
        layout.setBackground(gd);
    }

    @Override
    public void setEditorCompletion(@NonNull EditorAutoCompletion completion) {
        editorAutoCompletion = completion;
    }

    @NonNull
    @Override
    public View inflate(@NonNull Context context) {
        this.context = context;
        RelativeLayout layout2 = new RelativeLayout(context);
        listView = new ListView(context);
        layout2.addView(listView, new LinearLayout.LayoutParams(-1, -1));
        progressBar = new ProgressBar(context);
        layout2.addView(progressBar);
        var params = ((RelativeLayout.LayoutParams) progressBar.getLayoutParams());
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.width = params.height = (int) valueInDp(30);
        GradientDrawable gd = new GradientDrawable();
//        gd.setCornerRadius(valueInDp(10));
        layout2.setBackground(gd);
        layout = layout2;
        listView.setDividerHeight(0);
        setLoading(true);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            editorAutoCompletion.select(position);
        });

        setEnabledAnimation(false);

        return layout2;
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
            while(listView.getFirstVisiblePosition() + 1 > position && listView.canScrollList(-1)) {
                performScrollList(incrementPixels / 2);
            }
            while(listView.getFirstVisiblePosition() - 1 < position && listView.canScrollList(1)) {
                performScrollList(-incrementPixels / 2);
            }
        });
    }

    @Override
    public void setEnabledAnimation(boolean enabledAnimation) {
        this.enabledAnimation = enabledAnimation;

        if (enabledAnimation) {
            layout.setLayoutTransition(new LayoutTransition());
            var transition = layout.getLayoutTransition();
            transition.enableTransitionType(LayoutTransition.CHANGING);
            transition.enableTransitionType(LayoutTransition.APPEARING);
            transition.enableTransitionType(LayoutTransition.DISAPPEARING);
            transition.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
            transition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);

            listView.setLayoutTransition(layout.getLayoutTransition());
        } else  {
            layout.setLayoutTransition(null);
            listView.setLayoutTransition(null);
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

}
