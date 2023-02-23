package com.bluewhaleyt.codewhale;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;

import com.bluewhaleyt.common.DynamicColorsUtil;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.common.SDKUtil;

import java.util.Locale;

public class WhaleApplication extends Application {

    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        setupDynamicColor();
    }

    public static Context getContext() {
        return context;
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

    public static void updateLanguage(Context context, String lang) {
        switch (lang) {
            case "auto":
                setLocaleFollowSystem();
                break;
            case "zh_TW":
                setLocale(context, "zh", "TW");
                break;
            default:
                setLocale(context, lang);
        }
    }

    private static void setLocaleFollowSystem() {
        Locale locale;
        if (SDKUtil.isAtLeastSDK24()) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            locale = Resources.getSystem().getConfiguration().locale;
        }
        var systemLocale = new Locale(locale.getLanguage(), locale.getCountry());
        var config = Resources.getSystem().getConfiguration();
        config.setLocale(systemLocale);
    }

    private static void setLocale(Context context, String lang) {
        setLocale(context, new Locale(lang));
    }

    private static void setLocale(Context context, String lang, String country) {
        setLocale(context, new Locale(lang, country));
    }

    private static void setLocale(Context context, Locale locale) {
        var config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
