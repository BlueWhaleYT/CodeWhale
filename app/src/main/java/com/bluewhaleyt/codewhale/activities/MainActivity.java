package com.bluewhaleyt.codewhale.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluewhaleyt.codewhale.adapters.FilePagerAdapter;
import com.bluewhaleyt.codewhale.components.TreeView;
import com.bluewhaleyt.codewhale.databinding.DialogLayoutInputBinding;
import com.bluewhaleyt.codewhale.fragments.EditorFragment;
import com.bluewhaleyt.codewhale.tools.editor.basic.ThemeHandler;
import com.bluewhaleyt.codewhale.tools.editor.basic.languages.modules.AndroidJavaLanguage;
import com.bluewhaleyt.codewhale.tools.editor.textmate.CustomSyntaxHighlighter;
import com.bluewhaleyt.codewhale.utils.DialogUtil;
import com.bluewhaleyt.codewhale.utils.EditorUtil;
import com.bluewhaleyt.codewhale.utils.SharedPrefsUtil;
import com.bluewhaleyt.codewhale.utils.UriResolver;
import com.bluewhaleyt.common.CommonUtil;
import com.bluewhaleyt.common.DynamicColorsUtil;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.common.PermissionUtil;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.databinding.ActivityMainBinding;
import com.bluewhaleyt.codewhale.tools.editor.completion.EditorCompletionItemAdapter;
import com.bluewhaleyt.codewhale.tools.editor.completion.EditorCompletionLayout;
import com.bluewhaleyt.codewhale.utils.Constants;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.filemanagement.FileIconUtil;
import com.bluewhaleyt.filemanagement.FileUtil;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.rosemoe.sora.event.SelectionChangeEvent;
import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.Magnifier;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class MainActivity extends BaseActivity {

    private static ActivityMainBinding binding;
    private Intent intent;

    private DialogUtil dialogUtil;
    private EditorUtil editorUtil;
    private SharedPrefsUtil sharedPrefsUtil;
    private SharedPreferences sharedPrefs;

    private AlertDialog dialog;

    private TreeView.TreeViewAdapter adapterTreeView;
    private List<TreeView.TreeNode> nodesTreeView;
    private TreeView.TreeNode<TreeView.Dir> nodeTreeView;

    private RecyclerView rvTreeView;

    private List<String> filePaths;
    public static List<EditorFragment> editorFragments = new ArrayList<>();
    private FilePagerAdapter filePagerAdapter = new FilePagerAdapter(getSupportFragmentManager(), editorFragments);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        bindView(binding);

        editorUtil = new EditorUtil(this, binding.editor);
        editorUtil.setSymbolInputView(binding.symbolInputView);

        if (PermissionUtil.isAlreadyGrantedExternalStorageAccess()) {
//            setEditorContentFromFile(PreferencesManager.getRecentOpenFile());

            var path = PreferencesManager.getRecentOpenFolder();
            rvTreeView = binding.navigationView.getHeaderView(0).findViewById(R.id.rvFileList);
            setupTreeView(path, rvTreeView, FileUtil.getFileNameOfPath(path));

            setupTabView();
        }

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
            case R.id.menu_save_file:
                saveFile(PreferencesManager.getRecentOpenFile());
                break;
            case R.id.menu_close_file:
                closeFileTab();
                break;
            case R.id.menu_open_file:
                openFileChooser();
                break;
            case R.id.menu_open_folder:
                openFolderChooser();
                break;
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

        setupMoveSelectionEvent();

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

        updateEditorState();
        setupToolbar();

        var recentFile = PreferencesManager.getRecentOpenFile();
        binding.layoutEmptyFiles.btnOpenRecentFile.setText(
                getString(R.string.open_recent_file) + ":\n" + FileUtil.getFileNameOfPath(recentFile)
        );
        binding.layoutEmptyFiles.btnOpenRecentFile.setOnClickListener(v -> openFile(recentFile));
        binding.layoutEmptyFiles.btnOpenFolder.setOnClickListener(v -> openFolderChooser());
        binding.layoutEmptyFiles.btnGitClone.setOnClickListener(v -> cloneGitRepo());

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
        binding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        drawerToggle.syncState();

        disableNavScroll(binding.navigationView);
        binding.navigationView.getHeaderView(0).findViewById(R.id.btnClose).setOnClickListener(v -> binding.drawerLayout.close());
    }

    private void disableNavScroll(NavigationView navView) {
        NavigationMenuView navMenu = (NavigationMenuView) navView.getChildAt(0);
        navMenu.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
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

        // set tab layout color
        binding.viewPager.setBackgroundColor(colorBg);
        binding.tabLayoutFiles.setBackgroundColor(colorBg);
        binding.tabLayoutFiles.setTabTextColors(colorText, new DynamicColorsUtil(this).getColorPrimary());
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
            ThemeHandler.setTheme(this, binding.editor, PreferencesManager.getEditorTheme(), ThemeHandler.THEME_TEXTMATE, PreferencesManager.getRecentOpenFile());

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

        if (!filePath.equals("")) {
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
                        openFile(clickedPath);
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
                    final ImageView imageView = dirViewHolder.getImageView();
                    int img = isExpand ? R.drawable.ic_baseline_keyboard_arrow_down_24 : R.drawable.ic_baseline_keyboard_arrow_right_24;
                    int img2 = isExpand ? R.drawable.ic_baseline_folder_open_24 : R.drawable.ic_baseline_folder_24;
                    ivArrow.setImageResource(img);
                    imageView.setImageResource(img2);
                }

                @Override
                public void onLongClick(String clickedPath) {

                }
            });
            recyclerView.setAdapter(adapterTreeView);
        }

        var emptyText = binding.navigationView.getHeaderView(0).findViewById(R.id.tvNoData);
        if (!PreferencesManager.getRecentOpenFolder().equals("")) {
            emptyText.setVisibility(View.GONE);
        } else emptyText.setVisibility(View.VISIBLE);

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

    private void setupTabView() {
        binding.viewPager.setAdapter(filePagerAdapter);
        binding.tabLayoutFiles.setupWithViewPager(binding.viewPager);

        loadFileTab();

        Runtime.getRuntime().addShutdownHook(new Thread(this::saveFileTab));

        binding.tabLayoutFiles.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                var position = tab.getPosition();
                var filePath = editorFragments.get(position).getFilePath();
                binding.viewPager.setCurrentItem(position);
                setEditorContentFromFile(filePath);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void addFileTab(String path) {
        var fileFragment = new EditorFragment(path);
        editorFragments.add(fileFragment);
        filePagerAdapter.notifyDataSetChanged();
        binding.tabLayoutFiles.selectTab(binding.tabLayoutFiles.getTabAt(editorFragments.size() - 1));
        saveFileTab();
        updateEditorState();
    }

    private void removeFileTab(int position) {
        editorFragments.remove(position);
        filePagerAdapter.notifyDataSetChanged();
        saveFileTab();
        updateEditorState();
    }

    private void saveFileTab() {
        var prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> filePaths = new HashSet<>();
        for (EditorFragment fragment : editorFragments) {
//            filePaths.add(fragment.getArguments().getString("filePath"));
            filePaths.add(fragment.getFilePath());
        }
        editor.putStringSet("filePaths", filePaths);
        editor.apply();
    }

    private void loadFileTab() {
        var prefs = getPreferences(Context.MODE_PRIVATE);
        Set<String> filePaths = prefs.getStringSet("filePaths", new HashSet<>());
        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.exists()) {
                EditorFragment editorFragment = new EditorFragment(file.getAbsolutePath());
                Bundle args = new Bundle();
                args.putString("filePath", filePath);
                editorFragment.setArguments(args);
                editorFragments.add(editorFragment);
                filePagerAdapter.notifyDataSetChanged();
                var position = binding.tabLayoutFiles.getSelectedTabPosition();
                binding.tabLayoutFiles.selectTab(binding.tabLayoutFiles.getTabAt(position));
                setEditorContentFromFile(editorFragments.get(position).getFilePath());
//                binding.tabLayoutFiles.addTab(binding.tabLayoutFiles.newTab().setText(file.getName()));
            }
        }
    }

    private void closeFileTab() {
        var position = binding.tabLayoutFiles.getSelectedTabPosition();
        if (filePagerAdapter.getCount() > 0) {
            removeFileTab(position);
        }
    }

    private void updateEditorState() {
        if (filePagerAdapter.isEmpty()) {
            binding.editor.setVisibility(View.GONE);
            binding.hscrollSymbolView.setVisibility(View.GONE);
            binding.layoutMoveSelection.setVisibility(View.GONE);
            binding.tabLayoutFiles.setVisibility(View.GONE);
            binding.layoutEmptyFiles.getRoot().setVisibility(View.VISIBLE);
        } else {
            binding.editor.setVisibility(View.VISIBLE);
            binding.hscrollSymbolView.setVisibility(View.VISIBLE);
            binding.layoutMoveSelection.setVisibility(View.VISIBLE);
            binding.tabLayoutFiles.setVisibility(View.VISIBLE);
            binding.layoutEmptyFiles.getRoot().setVisibility(View.GONE);
        }
    }

    public static EditorColorScheme getEditorColorScheme() {
        return binding.editor.getColorScheme();
    }

    private void setEditorContentFromFile(String path) {
        editorUtil.setText(FileUtil.readFile(path));

        if (PreferencesManager.isTextmateEnabled()) {
            ThemeHandler.setTheme(this, binding.editor, PreferencesManager.getEditorTheme(), ThemeHandler.THEME_TEXTMATE, FileUtil.getFileNameOfPath(path));
        }

        // save recent open file
        sharedPrefsUtil = new SharedPrefsUtil(this, PreferencesManager.getRecentOpenFileKey(), path);
        sharedPrefsUtil.saveData();
    }

    private void saveFile(String path) {
        if (FileUtil.isFileExist(path)){
            FileUtil.writeFile(path, binding.editor.getText().toString());
            SnackbarUtil.makeSnackbar(this, getString(R.string.file_save));
        }
    }

    private void createFile() {
        // TODO
    }

    private void createFolder() {
        // TODO
    }

    private void createNewFile() {
        // TODO
    }

    private void openFile(String path) {
        addFileTab(path);
        setEditorContentFromFile(path);
    }

    private void cloneGitRepo() {
        DialogLayoutInputBinding binding = DialogLayoutInputBinding.inflate(getLayoutInflater());
        binding.textInputLayout.setHint(R.string.remote_repo);
        var builder = new AlertDialog.Builder(this)
                .setTitle(R.string.git_clone_repo)
                .setMessage(R.string.no_files_picked)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.clone, (d, i) -> startCloneRepo())
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(R.string.choose_folder, null);
        dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            var btn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            btn.setOnClickListener(v -> openFolderChooserGit());
        });
        dialog.show();
    }

    private void openFileChooser() {
        intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        launchChooseFile.launch(intent);
    }

    private void openFolderChooser() {
        intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
        launchChooseFolder.launch(intent);
    }

    private void openFolderChooserGit() {
        intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
        launchChooseFolderGit.launch(intent);
    }

    public static String getFilePathFromUri(Context context, Uri uri) {
        return UriResolver.getPathFromUri(context, uri);
    }

    public static String getFilePathFromDirUri(Context context, Uri uri) {
        return UriResolver.getPathFromUri(context, getDocumentUriFromUri(uri));
    }

    public static Uri getDocumentUriFromUri(Uri uri) {
        return DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
    }

    private void startCloneRepo() {
        var message = (TextView) dialog.findViewById(android.R.id.message);
        if (message.getText().toString().equals(getString(R.string.no_files_picked))) {
            SnackbarUtil.makeErrorSnackbar(this, getString(R.string.no_files_picked));
        } else {
            SnackbarUtil.makeSnackbar(this, getString(R.string.coming_soon));
        }
    }

    ActivityResultLauncher<Intent> launchChooseFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    var path = result.getData().getData();
                    var pathConvert = getFilePathFromUri(this, path);
                    openFile(pathConvert);
                }
            }
    );

    ActivityResultLauncher<Intent> launchChooseFolder = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    var path = result.getData().getData();
                    var pathConvert = getFilePathFromDirUri(this, path);

                    sharedPrefsUtil = new SharedPrefsUtil(this, PreferencesManager.getRecentOpenFolderKey(), pathConvert);
                    sharedPrefsUtil.saveData();

                    setupTreeView(pathConvert, rvTreeView, FileUtil.getFileNameOfPath(pathConvert));
                }
            }
    );

    ActivityResultLauncher<Intent> launchChooseFolderGit = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    var path = result.getData().getData();
                    var pathConvert = getFilePathFromDirUri(this, path);
                    dialog.setMessage(getString(R.string.local_repo) + ":\n" + pathConvert);
                }
            }
    );

}