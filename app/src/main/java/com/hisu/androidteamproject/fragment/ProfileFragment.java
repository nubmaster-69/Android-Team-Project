package com.hisu.androidteamproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.hisu.androidteamproject.MainActivity;
import com.hisu.androidteamproject.R;

public class ProfileFragment extends Fragment {

    private ImageButton ibtnAdd;
    private MainActivity containerActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstancesState) {
        View profileView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        initFragmentUI(profileView);

        ibtnAdd.setOnClickListener(view -> switchToAddPostScreen());

        return profileView;
    }

    private void initFragmentUI(View profileView) {
        containerActivity = (MainActivity) getActivity();
        ibtnAdd = profileView.findViewById(R.id.ibtnAdd);
    }

    public void switchToAddPostScreen() {
        containerActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(containerActivity.getFrmContainer().getId(), new AddPostFragment())
                .commit();
    }
}
