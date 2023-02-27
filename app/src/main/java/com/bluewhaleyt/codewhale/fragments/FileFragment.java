package com.bluewhaleyt.codewhale.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.filemanagement.FileUtil;

public class FileFragment extends Fragment {

    private String fileName;
    private String filePath;

    public FileFragment(String filePath) {
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
        View view = inflater.inflate(R.layout.fragment_file, container, false);

        Bundle args = getArguments();
        if(args != null) {
            filePath = args.getString("filePath");
        }
        return view;
    }
}