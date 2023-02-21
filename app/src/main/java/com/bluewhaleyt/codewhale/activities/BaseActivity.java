package com.bluewhaleyt.codewhale.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.bluewhaleyt.codewhale.WhaleApplication;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.common.CommonUtil;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.common.PermissionUtil;
import com.bluewhaleyt.component.dialog.DialogUtil;
import com.bluewhaleyt.crashdebugger.CrashDebugger;
import com.bluewhaleyt.codewhale.databinding.ActivityMainBinding;

public class BaseActivity extends AppCompatActivity {

    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashDebugger.init(this);
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

    public void bindView(ViewBinding binding) {
        setContentView(binding.getRoot());
        initialize();
    }

    private void initialize() {
        fixColorSurfaces();
        updateTheme();
    }

    public void fixColorSurfaces() {
        var color = CommonUtil.SURFACE_FOLLOW_WINDOW_BACKGROUND;
        CommonUtil.setStatusBarColorWithSurface(this, color);
        CommonUtil.setNavigationBarColorWithSurface(this, color);
        CommonUtil.setToolBarColorWithSurface(this, color);
    }

    private void requestPermission() {
        if (!PermissionUtil.isAlreadyGrantedExternalStorageAccess()) {
            dialogUtil = new DialogUtil(
                    this,
                    "Permission request",
                    "You need to grant the permissions before using the application."
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

}
