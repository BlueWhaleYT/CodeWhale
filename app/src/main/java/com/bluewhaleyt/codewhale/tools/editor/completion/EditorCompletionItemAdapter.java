package com.bluewhaleyt.codewhale.tools.editor.completion;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.databinding.LayoutEditorCompletionListItemBinding;
import com.bluewhaleyt.codewhale.tools.editor.basic.languages.LanguageHandler;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.common.CommonUtil;
import com.bluewhaleyt.common.DynamicColorsUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.rosemoe.sora.lang.completion.CompletionItem;
import io.github.rosemoe.sora.widget.component.EditorCompletionAdapter;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class EditorCompletionItemAdapter extends EditorCompletionAdapter {

    private LayoutEditorCompletionListItemBinding binding;
    private CompletionItem item;

    @Override
    public int getItemHeight() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                65, getContext().getResources().getDisplayMetrics());
    }

    @Override
    protected View getView(int position, View convertView, ViewGroup parent, boolean isCurrentCursorPosition) {
        binding = LayoutEditorCompletionListItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        convertView = binding.getRoot();
        item = getItem(position);

        binding.tvLabel.setText(item.label);
        binding.tvLabel.setTextColor(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_PRIMARY));

        binding.tvDesc.setText(item.desc);
        binding.tvDesc.setTextColor(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_SECONDARY));

        convertView.setTag(position);
        if (isCurrentCursorPosition) {
            convertView.setBackgroundColor(getThemeColor(EditorColorScheme.COMPLETION_WND_ITEM_CURRENT));
            binding.tvDesc.setVisibility(View.VISIBLE);
        } else {
            convertView.setBackgroundColor(0);
            binding.tvDesc.setVisibility(View.GONE);
        }

        if (PreferencesManager.isAutoCompletionFollowCursorEnabled()) {
            binding.tvDesc.setVisibility(View.GONE);
        }

        var type = item.desc.subSequence(0, 1);
        setupTypeIcon(type, binding.ivImage, binding.tvDesc);
        setPartialColorSpan(new LanguageHandler().getPrefix(), new DynamicColorsUtil(getContext()).getColorPrimary());

        return convertView;
    }

    private void setupTypeIcon(CharSequence type, ImageView imageView, TextView textView) {
        var context = getContext();
        var icon = 0;
        var color = getThemeColor(EditorColorScheme.TEXT_NORMAL);;
        var text = "";
        var desc = item.desc.toString().substring(1);
        switch (type.toString()) {
            case "K":
                icon = R.drawable.ic_intellisense_symbol_keyword;
                text = context.getString(R.string.keyword);
                break;
            case "I":
                icon = R.drawable.ic_intellisense_symbol_key;
                text = context.getString(R.string.identifier);
                break;
            case "S":
                icon = R.drawable.ic_intellisense_symbol_snippet;
                text = context.getString(R.string.snippet) + desc;
                break;
            case "M":
                icon = R.drawable.ic_intellisence_symbol_method;
                text = context.getString(R.string.method) + desc;
                color = CommonUtil.isInDarkMode(context) ? 0xFF843bbd : 0xFF652d90;
                break;
        }
        imageView.setImageResource(icon);
        imageView.setColorFilter(color);

        if (!PreferencesManager.isAutoCompletionFollowCursorEnabled()) textView.setText(text);
    }

    private void setPartialColorSpan(String partial, int color) {
        if (partial != null && partial.length() > 0) {
            var span = new SpannableString(item.label);
            if (item.label.toString().startsWith(partial)) {
                setSpan(span, color, 0, partial.length());
            } else if (item.label.toString().contains(partial)) {
                var pattern = Pattern.compile(partial);
                var matcher = pattern.matcher(span);
                while (matcher.find()) {
                    setSpan(span, color, matcher.start(), matcher.end());
                }
            }
            binding.tvLabel.setText(span);
        }
    }

    private void setSpan(SpannableString span, int color, int start, int end) {
        span.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}
