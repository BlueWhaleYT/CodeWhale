package com.bluewhaleyt.codewhale;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.bluewhaleyt.common.DynamicColorsUtil;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.common.SDKUtil;

public class WhaleApplication extends Application {

    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        setupDynamicColor();
    }

    private void setupDynamicColor() {
        if (PreferencesManager.isDynamicColorEnabled()) {
            DynamicColorsUtil.setDynamicColorsIfAvailable(this);
        }
    }

    public static void updateTheme(String value) {
        switch (value) {
            case "auto":
                if (SDKUtil.isAtLeastSDK29()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

    public static Context getContext() {
        return context;
    }
}
