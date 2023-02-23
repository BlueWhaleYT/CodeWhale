package com.bluewhaleyt.codewhale.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtil {

    private Context context;
    private SharedPreferences sharedPrefs;
    private String keyName, keyValue, defaultKeyValue;

    public SharedPrefsUtil(Context context, String keyName, String keyValue) {
        this.context = context;
        this.keyName = keyName;
        this.keyValue = keyValue;
        this.defaultKeyValue = keyValue;
    }

    public SharedPrefsUtil(Context context, String keyName, String keyValue, String defaultKeyValue) {
        this.context = context;
        this.keyName = keyName;
        this.keyValue = keyValue;
        this.defaultKeyValue = defaultKeyValue;
    }

    public void saveData() {
        sharedPrefs = context.getSharedPreferences(keyName, Context.MODE_PRIVATE);
        var editor = sharedPrefs.edit();
        editor.putString(keyName, keyValue);
        editor.commit();
    }

    public String getData() {
        sharedPrefs = context.getSharedPreferences(keyName, Context.MODE_PRIVATE);
        return sharedPrefs.getString(keyName, defaultKeyValue);
    }

}
