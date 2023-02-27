package com.bluewhaleyt.codewhale.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bluewhaleyt.codewhale.fragments.EditorFragment;

import java.util.List;

public class FilePagerAdapter extends FragmentPagerAdapter {

    private List<EditorFragment> editorFragments;

    public FilePagerAdapter(FragmentManager fm, List<EditorFragment> fragments) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        editorFragments = fragments;
    }

    public void setFileFragments(List<EditorFragment> editorFragments) {
        this.editorFragments = editorFragments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return editorFragments.get(position);
    }

    @Override
    public int getCount() {
        return editorFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return editorFragments.get(position).getFileName();
    }
}