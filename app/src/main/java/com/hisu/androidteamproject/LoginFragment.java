package com.hisu.androidteamproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    private TextView txtRegisterNow;
    private MainActivity containerActivity;
    private Button btnLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        containerActivity = (MainActivity) getActivity();
        View loginView = inflater.inflate(R.layout.fragment_login, container, false);

        txtRegisterNow = loginView.findViewById(R.id.txvRegisterNow);

        txtRegisterNow.setOnClickListener(view -> {
            containerActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                    .replace(containerActivity.getFrmContainer().getId(), new RegisterFragment())
                    .commit();
        });

        btnLogin = loginView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> containerActivity.setFragment(new NewFeedFragment()));

        return loginView;
    }
}