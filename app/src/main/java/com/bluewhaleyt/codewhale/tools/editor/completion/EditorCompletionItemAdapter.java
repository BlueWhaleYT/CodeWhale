package com.bluewhaleyt.codewhale.tools.editor.completion;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.databinding.LayoutEditorCompletionListItemBinding;

import io.github.rosemoe.sora.lang.completion.CompletionItem;
import io.github.rosemoe.sora.widget.component.EditorCompletionAdapter;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class EditorCompletionItemAdapter extends EditorCompletionAdapter {

    private LayoutEditorCompletionListItemBinding binding;
    private CompletionItem item;

    @Override
    public int getItemHeight() {
        return (int)
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        40,
                        getContext().getResources().getDisplayMetrics());
    }

    @Override
    protected View getView(int position, View convertView, ViewGroup parent, boolean isCurrentCursorPosition) {
        binding = LayoutEditorCompletionListItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        convertView = binding.getRoot();
        var item = getItem(position);

        binding.tvLabel.setText(item.label);
        binding.tvLabel.setTextColor(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_PRIMARY));

        binding.tvDesc.setText(item.desc);
        binding.tvDesc.setTextColor(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_SECONDARY));

        convertView.setTag(position);
        if (isCurrentCursorPosition) {
            convertView.setBackgroundColor(getThemeColor(EditorColorScheme.COMPLETION_WND_ITEM_CURRENT));
        } else {
            convertView.setBackgroundColor(0);
        }

        var type = item.desc.subSequence(0, 1);
        setupTypeIcon(type, binding.tvImage);
        binding.tvImage.setColorFilter(getThemeColor(EditorColorScheme.TEXT_NORMAL));

        return convertView;
    }

    private void setupTypeIcon(CharSequence type, ImageView imageView) {
        int icon = 0;
        switch (type.toString()) {
            case "K":
                icon = R.drawable.ic_intellisense_symbol_keyword;
                break;
            case "S":
                icon = R.drawable.ic_intellisense_symbol_snippet;
                break;
            case "I":
                icon = R.drawable.ic_intellisense_symbol_key;
                break;
        }
        imageView.setImageResource(icon);
    }
}
