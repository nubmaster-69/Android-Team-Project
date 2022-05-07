package com.hisu.androidteamproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.hisu.androidteamproject.R;

public class AddPostFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstancesState) {
        View addPostView = inflater.inflate(R.layout.fragment_add_post, container, false);

        return addPostView;
    }
}
