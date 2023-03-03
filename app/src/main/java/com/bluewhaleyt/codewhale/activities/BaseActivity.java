package com.bluewhaleyt.codewhale.activities;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.WhaleApplication;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.common.CommonUtil;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.common.PermissionUtil;
import com.bluewhaleyt.common.SDKUtil;
import com.bluewhaleyt.component.dialog.DialogUtil;
import com.bluewhaleyt.crashdebugger.CrashDebugger;
import com.bluewhaleyt.codewhale.databinding.ActivityMainBinding;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashDebugger.init(this);
        updateLanguage();
//        getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermission();
    }

    @Override
    public void finish() {
        super.finish();
        IntentUtil.finishTransition(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void bindView(ViewBinding binding) {
        setContentView(binding.getRoot());
        initialize();
    }

    private void initialize() {
        fixColorSurfaces();
        updateTheme();
    }

    public void fixColorSurfaces() {
        Class<?> clz = getClass();
        var color = CommonUtil.SURFACE_FOLLOW_WINDOW_BACKGROUND;
        if (clz != MainActivity.class) {
            CommonUtil.setToolBarColorWithSurface(this, color);
        }
        CommonUtil.setStatusBarColorWithSurface(this, color);
        CommonUtil.setNavigationBarColorWithSurface(this, color);
    }

    private void requestPermission() {
        if (!PermissionUtil.isAlreadyGrantedExternalStorageAccess()) {
            dialogUtil = new DialogUtil(
                    this,
                    getString(R.string.permission_request),
                    getString(R.string.permission_request_desc)
            );
            dialogUtil.setPositiveButton(android.R.string.ok, (d, i) -> PermissionUtil.requestAllFileAccess(this));
            dialogUtil.setNegativeButton(android.R.string.cancel, null);
            dialogUtil.setCancelable(false);
            dialogUtil.build();
        }
    }

    private void updateTheme() {
        WhaleApplication.updateTheme(PreferencesManager.getAppTheme());
    }

    public void updateLanguage() {
        WhaleApplication.updateLanguage(this, PreferencesManager.getAppLanguage());
    }

}
