package com.hisu.androidteamproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RegisterFragment extends Fragment {

    private TextView txtLoginNow;
    private MainActivity containerActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        containerActivity = (MainActivity) getActivity();

        View registerView =
                inflater.inflate(R.layout.fragment_register, container, false);

        txtLoginNow = registerView.findViewById(R.id.txt_login_now);
        txtLoginNow.setOnClickListener(view -> {
            containerActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                    .replace(containerActivity.getFrmContainer().getId(), new LoginFragment())
                    .commit();
        });

        return registerView;
    }
}