package com.bluewhaleyt.codewhale.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.filemanagement.FileUtil;

public class EditorFragment extends Fragment {

    private String fileName;
    private String filePath;

    public EditorFragment(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return FileUtil.getFileNameOfPath(filePath);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout_editor, container, false);

        Bundle args = getArguments();
        if(args != null) {
            filePath = args.getString("filePath");
        }
        return view;
    }
}