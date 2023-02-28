package com.bluewhaleyt.codewhale.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import com.bluewhaleyt.codewhale.databinding.DialogLayoutInputBinding;
import com.google.android.material.textfield.TextInputLayout;

public class DialogUtil {

   private com.bluewhaleyt.component.dialog.DialogUtil dialogUtil;

   private DialogLayoutInputBinding bindingDialogInput;

   private Context context;

    public DialogUtil(Context context) {
        this.context = context;
        dialogUtil = new com.bluewhaleyt.component.dialog.DialogUtil(context);
        bindingDialogInput = DialogLayoutInputBinding.inflate(getLayoutInflater());
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(context);
    }

    public com.bluewhaleyt.component.dialog.DialogUtil getDialogUtil() {
        return dialogUtil;
    }

    public TextInputLayout getInputLayout() {
        return bindingDialogInput.textInputLayout;
    }

    public void createInputDialog() {
        getDialogUtil().setView(bindingDialogInput.getRoot());
    }

    public void setInputLayoutHint(String hint) {
        getInputLayout().setHint(hint);
    }

}
