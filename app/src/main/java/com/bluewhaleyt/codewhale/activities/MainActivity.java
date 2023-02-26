package com.bluewhaleyt.codewhale.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluewhaleyt.codewhale.components.TreeView;
import com.bluewhaleyt.codewhale.tools.editor.basic.ThemeHandler;
import com.bluewhaleyt.codewhale.tools.editor.basic.languages.modules.AndroidJavaLanguage;
import com.bluewhaleyt.codewhale.tools.editor.textmate.CustomSyntaxHighlighter;
import com.bluewhaleyt.codewhale.utils.AssetsFileLoader;
import com.bluewhaleyt.codewhale.utils.EditorUtil;
import com.bluewhaleyt.common.CommonUtil;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.common.PermissionUtil;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.databinding.ActivityMainBinding;
import com.bluewhaleyt.codewhale.tools.editor.completion.EditorCompletionItemAdapter;
import com.bluewhaleyt.codewhale.tools.editor.completion.EditorCompletionLayout;
import com.bluewhaleyt.codewhale.utils.Constants;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.filemanagement.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.rosemoe.sora.event.SelectionChangeEvent;
import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.Magnifier;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class MainActivity extends BaseActivity {

    private static ActivityMainBinding binding;

    private EditorUtil editorUtil;

    private TreeView.TreeViewAdapter adapterTreeView;
    private List<TreeView.TreeNode> nodesTreeView;
    private TreeView.TreeNode<TreeView.Dir> nodeTreeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        bindView(binding);

        editorUtil = new EditorUtil(this, binding.editor);
        editorUtil.setSymbolInputView(binding.symbolInputView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialize();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            MenuBuilder builder = (MenuBuilder) menu;
            builder.setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
//                setupSearchPanel();
                break;
            case R.id.menu_settings:
                IntentUtil.intent(this, SettingsActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {

        setupAppToolbar();
        setupAppDrawer();

        setupAutoComplete();
        setupMagnifier();
        setEditorText(AssetsFileLoader.getAssetsFileContent(this, "presets/test.java"));

        setupMoveSelectionEvent();
        setupToolbar();

        if (PermissionUtil.isAlreadyGrantedExternalStorageAccess()) {
            var path = FileUtil.getExternalStoragePath() + "/Documents";
            RecyclerView recyclerview = binding.navigationView.getHeaderView(0).findViewById(R.id.rvFileList);
            setupTreeView(path, recyclerview, FileUtil.getFileNameOfPath(path));
        }

        if (PreferencesManager.isSyntaxHighlightingEnabled()) {
            if (PreferencesManager.isTextmateEnabled()) setupTextmateHighlight();
            else setupNormalHighlight();
        } else {
            setNoSyntaxHighlighting();
        }

        if (PreferencesManager.isFollowEditorThemeEnabled()) setColorSurfacesFollowEditorTheme();
        else {
            fixColorSurfaces();
            fixColorSurfaces2();
        }

        editorUtil = new EditorUtil(this, binding.editor, binding.editor.getColorScheme());
        editorUtil.setNonPrintFlag();
        editorUtil.setup();

    }

    private void setupAppToolbar() {
        setSupportActionBar(binding.toolbar);
    }

    private void setupAppDrawer() {
        var drawerToggle = new ActionBarDrawerToggle(
                        this,
                        binding.drawerLayout,
                        binding.toolbar,
                        R.string.open,
                        R.string.close);
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void setColorSurfacesFollowEditorTheme() {
        var colorBg = binding.editor.getColorScheme().getColor(EditorColorScheme.WHOLE_BACKGROUND);
        var colorBgHc = binding.editor.getColorScheme().getColor(EditorColorScheme.CURRENT_LINE);
        var colorText = binding.editor.getColorScheme().getColor(EditorColorScheme.TEXT_NORMAL);

        if (PreferencesManager.isSymbolInputEnabled() || PreferencesManager.isSelectionActionEnabled()) {
            getWindow().setNavigationBarColor(colorBgHc);
        } else {
            getWindow().setNavigationBarColor(colorBg);
        }

        getWindow().setStatusBarColor(colorBg);

        // set toolbar bg color and title color according to the editor themes
        var text = new SpannableString(binding.toolbar.getTitle());
        text.setSpan(new ForegroundColorSpan(colorText), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        binding.toolbar.setTitle(text);
        binding.toolbar.setBackground(new ColorDrawable(colorBg));

        // set symbol input view bg color according to the editor theme
        binding.symbolInputView.setBackgroundColor(colorBgHc);
        binding.symbolInputView.setTextColor(colorText);

        binding.layoutMoveSelection.setBackgroundColor(colorBgHc);
        binding.btnLeft.setColorFilter(colorText);
        binding.btnRight.setColorFilter(colorText);
        binding.btnUp.setColorFilter(colorText);
        binding.btnDown.setColorFilter(colorText);
        binding.btnDuplicateLine.setColorFilter(colorText);

        // set drawer bg color
        binding.navigationView.setBackgroundColor(colorBg);

        // set search panel bg color
        binding.layoutSearchPanel.searchPanel.setBackgroundColor(colorBg);
        binding.layoutReplacePanel.replacePanel.setBackgroundColor(colorBg);
        binding.layoutSearchPanel.etSearch.setBackgroundColor(colorBgHc);
        binding.layoutReplacePanel.etReplace.setBackgroundColor(colorBgHc);

    }

    private void setupNormalHighlight() {
//        var scheme = CommonUtil.isInDarkMode(this) ? new SchemeMaterialPalenight() : new SchemeEclipse();
//        binding.editor.setColorScheme(scheme);
        ThemeHandler.setTheme(this, binding.editor, PreferencesManager.getEditorTheme(), ThemeHandler.THEME_NORMAL, Constants.TEST_SYNTAX);

        Language language;
        if (PreferencesManager.isLanguageJavaEnabled()) {
//            language = new JavaLanguage();
            language = new AndroidJavaLanguage();
        } else {
            language = new EmptyLanguage();
        }
        binding.editor.setEditorLanguage(language);
    }

    private void setupTextmateHighlight() {
        try {
            ThemeHandler.setTheme(this, binding.editor, PreferencesManager.getEditorTheme(), ThemeHandler.THEME_TEXTMATE, Constants.TEST_SYNTAX);

            CustomSyntaxHighlighter customHighlighter = new CustomSyntaxHighlighter();
            customHighlighter.applyLanguages(binding.editor);

        } catch (Exception e) {
            SnackbarUtil.makeErrorSnackbar(this, e.getMessage(), e.toString());
        }
    }

    private void setNoSyntaxHighlighting() {
        ThemeHandler.setTheme(this, binding.editor, PreferencesManager.getEditorTheme(), ThemeHandler.THEME_NORMAL, Constants.TEST_SYNTAX);
        binding.editor.setEditorLanguage(new EmptyLanguage());
    }

    private void setEditorText(String text) {
        editorUtil.setText(text);
    }

    public void fixColorSurfaces2() {
        binding.symbolInputView.setBackgroundColor(CommonUtil.SURFACE_FOLLOW_WINDOW_BACKGROUND);
    }

    private void setupAutoComplete() {
        if (PreferencesManager.isAutoCompletionFollowCursorEnabled()) {
            binding.editor.setCompletionWndPositionMode(CodeEditor.WINDOW_POS_MODE_FOLLOW_CURSOR_ALWAYS);
        } else {
            binding.editor.setCompletionWndPositionMode(0);
        }

        binding.editor.getComponent(EditorAutoCompletion.class).setLayout(new EditorCompletionLayout());
        binding.editor.getComponent(EditorAutoCompletion.class).setAdapter(new EditorCompletionItemAdapter());
        binding.editor.getComponent(EditorAutoCompletion.class).setEnabledAnimation(PreferencesManager.isAutoCompletionAnimationEnabled());
        binding.editor.getComponent(EditorAutoCompletion.class).setEnabled(PreferencesManager.isAutoCompletionEnabled());
        binding.editor.subscribeEvent(SelectionChangeEvent.class, (event, unsubscribe) -> {
            var editorAutoCompletion = binding.editor.getComponent(EditorAutoCompletion.class);
            CommonUtil.waitForTimeThenDo(1, () -> {
                if (editorAutoCompletion.getCurrentPosition() <= -1) {
                    editorAutoCompletion.moveDown();
                }
                return null;
            });
        });
    }

    private void setupMoveSelectionEvent() {
        var editor = binding.editor;
        binding.btnLeft.setOnClickListener(v -> editor.moveSelectionLeft());
        binding.btnRight.setOnClickListener(v -> editor.moveSelectionRight());
        binding.btnUp.setOnClickListener(v -> editor.moveSelectionUp());
        binding.btnDown.setOnClickListener(v -> editor.moveSelectionDown());
        binding.btnDuplicateLine.setOnClickListener(v -> editor.duplicateLine());
    }

    private void setupToolbar() {
        if (PreferencesManager.isSymbolInputEnabled()) {
            binding.hscrollSymbolView.setVisibility(View.VISIBLE);
        } else {
            binding.hscrollSymbolView.setVisibility(View.GONE);
        }
        if (PreferencesManager.isSelectionActionEnabled()) {
            binding.layoutMoveSelection.setVisibility(View.VISIBLE);
        } else {
            binding.layoutMoveSelection.setVisibility(View.GONE);
        }
    }

    private void setupMagnifier() {
        var scaleSaved = Float.parseFloat(PreferencesManager.getMagnifierScale());
        var scale = 1.25f;
        if (scaleSaved <= 1) {
            binding.editor.getComponent(Magnifier.class).setScaleFactor(scale);
            SnackbarUtil.makeErrorSnackbar(this, getString(R.string.magnifier_resolve, "<=1"));
        } else {
            binding.editor.getComponent(Magnifier.class).setScaleFactor(scaleSaved);
        }
    }

//    private void setupSearchPanel() {
//        var bindingSearch = binding.layoutSearchPanel;
//        var bindingReplace = binding.layoutReplacePanel;
//        var searcher = binding.editor.getSearcher();
//
//        binding.layoutSearchPanel.getRoot().setVisibility(View.VISIBLE);
//        bindingSearch.imgBtnPrev.setEnabled(false);
//        bindingSearch.imgBtnNext.setEnabled(false);
//        bindingReplace.imgBtnReplace.setEnabled(false);
//        bindingReplace.imgBtnReplaceAll.setEnabled(false);
//
//        bindingSearch.imgBtnPrev.setOnClickListener(v -> searcher.gotoPrevious());
//        bindingSearch.imgBtnNext.setOnClickListener(v -> searcher.gotoNext());
//        bindingReplace.imgBtnReplace.setOnClickListener(v -> searcher.replaceThis(bindingReplace.etReplace.getText().toString()));
//        bindingReplace.imgBtnReplaceAll.setOnClickListener(v -> searcher.replaceAll(bindingReplace.etReplace.getText().toString()));
//
//        bindingSearch.etSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                updateSearchBtnState();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!s.toString().isEmpty()) {
//                    try {
//                        binding.editor
//                                .getSearcher()
//                                .search(
//                                        s.toString(),
//                                        new EditorSearcher.SearchOptions(true, true));
//                    } catch (PatternSyntaxException e) {
//                        SnackbarUtil.makeErrorSnackbar(MainActivity.this, e.getMessage(), e.toString());
//                    }
//                } else {
//                    binding.editor.getSearcher().stopSearch();
//                }
//            }
//        });
//
//        bindingReplace.etReplace.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                updateSearchBtnState();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        bindingSearch.imgBtnExpand.setOnClickListener(v -> {
//            binding.layoutReplacePanel.getRoot().setVisibility(View.VISIBLE);
//        });
//
//    }
//
//    private void updateSearchBtnState() {
//        var bindingSearch = binding.layoutSearchPanel;
//        var bindingReplace = binding.layoutReplacePanel;
//        if (bindingReplace.etReplace.getText().toString().isEmpty()) {
//            bindingReplace.imgBtnReplace.setEnabled(false);
//            bindingReplace.imgBtnReplaceAll.setEnabled(false);
//        } else if (!bindingReplace.etReplace.getText().toString().isEmpty()
//                && bindingSearch.etSearch.getText().toString().isEmpty()) {
//            bindingReplace.imgBtnReplace.setEnabled(false);
//            bindingReplace.imgBtnReplaceAll.setEnabled(false);
//        } else {
//            bindingReplace.imgBtnReplace.setEnabled(true);
//            bindingReplace.imgBtnReplaceAll.setEnabled(true);
//        }
//        if (bindingSearch.etSearch.getText().toString().isEmpty()
//                && bindingSearch.etSearch.getText().toString().isEmpty()) {
//            bindingSearch.imgBtnPrev.setEnabled(false);
//            bindingSearch.imgBtnNext.setEnabled(false);
//        } else {
//            bindingSearch.imgBtnPrev.setEnabled(true);
//            bindingSearch.imgBtnNext.setEnabled(true);
//        }
//    }

    private void setupTreeView(String filePath, RecyclerView recyclerView, String rootDirName) {

        TreeView.isPath = true;
        nodesTreeView = new ArrayList<>();
        nodeTreeView = new TreeView.TreeNode<>(new TreeView.Dir(rootDirName));
        nodesTreeView.add(nodeTreeView);

        setupTreeViewData(filePath, nodeTreeView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterTreeView = new TreeView.TreeViewAdapter(nodesTreeView, Arrays.asList(new TreeView.FileNodeBinder(), new TreeView.DirectoryNodeBinder()));

        adapterTreeView.setOnTreeNodeListener(new TreeView.TreeViewAdapter.OnTreeNodeListener() {
            @Override
            public boolean onClick(String clickedPath, TreeView.TreeNode node, RecyclerView.ViewHolder holder) {
                if (!node.isLeaf()) onToggle(!node.isExpand(), holder);
                if (FileUtil.isFile(clickedPath)) {
                    editorUtil.setText(FileUtil.readFile(clickedPath));
                    ThemeHandler.setTheme(getApplicationContext(), binding.editor, PreferencesManager.getEditorTheme(), ThemeHandler.THEME_TEXTMATE, FileUtil.getFileNameOfPath(clickedPath));

                } else {
                    if (FileUtil.isDirectory(clickedPath)) {

                    }
                }
                return false;
            }

            @Override
            public void onToggle(boolean isExpand, RecyclerView.ViewHolder holder) {
                var dirViewHolder = (TreeView.DirectoryNodeBinder.ViewHolder) holder;
                final ImageView ivArrow = dirViewHolder.getIvArrow();
                int img = isExpand ? R.drawable.ic_baseline_keyboard_arrow_down_24 : R.drawable.ic_baseline_keyboard_arrow_right_24;
                ivArrow.setImageResource(img);
            }

            @Override
            public void onLongClick(String clickedPath) {

            }
        });

        recyclerView.setAdapter(adapterTreeView);

    }

    private void setupTreeViewData(String filePath, TreeView.TreeNode<TreeView.Dir> dir) {
        final String[] path = {filePath};
        new Thread(() -> {
            Looper.prepare();
            var rootDir = new ArrayList<>();
            try {
                FileUtil.listDirectories(path[0], rootDir);
            } catch (Exception e) {
                //
            }

            for (Object obj : rootDir) {
                var file = obj.toString();
                if (FileUtil.isFile(file)) dir.addChild(new TreeView.TreeNode<>(new TreeView.File(file)));
                else if (FileUtil.isDirectory(file)) {
                    var dirTree = new TreeView.TreeNode<>(new TreeView.Dir(file));
                    dir.addChild(dirTree);
                    setupTreeViewData(file, dirTree);
                }
            }
        }).start();
    }

    public static EditorColorScheme getEditorColorScheme() {
        return binding.editor.getColorScheme();
    }

}