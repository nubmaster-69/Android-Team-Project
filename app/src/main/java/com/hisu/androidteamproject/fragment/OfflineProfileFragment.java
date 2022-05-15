package com.hisu.androidteamproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.hisu.androidteamproject.R;

public class OfflineProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState) {
        return inflater.inflate(R.layout.fragment_offline_user_profile, container, false);
    }
}