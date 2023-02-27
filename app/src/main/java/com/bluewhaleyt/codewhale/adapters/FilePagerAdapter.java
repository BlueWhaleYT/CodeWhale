package com.bluewhaleyt.codewhale.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bluewhaleyt.codewhale.activities.MainActivity;
import com.bluewhaleyt.codewhale.fragments.FileFragment;

import java.util.ArrayList;
import java.util.List;

public class FilePagerAdapter extends FragmentPagerAdapter {

    private List<FileFragment> fileFragments;

    public FilePagerAdapter(FragmentManager fm, List<FileFragment> fragments) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fileFragments = fragments;
    }

    public void setFileFragments(List<FileFragment> fileFragments) {
        this.fileFragments = fileFragments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fileFragments.get(position);
    }

    @Override
    public int getCount() {
        return fileFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fileFragments.get(position).getFileName();
    }
}