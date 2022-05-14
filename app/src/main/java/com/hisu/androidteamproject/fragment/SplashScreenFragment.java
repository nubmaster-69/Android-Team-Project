package com.hisu.androidteamproject.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hisu.androidteamproject.MainActivity;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.entity.User;

public class SplashScreenFragment extends Fragment {

    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View splashScreenView = inflater.inflate(R.layout.fragment_splash_screen, container, false);

        mainActivity = (MainActivity) getActivity();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

            if (user != null) {
                fireStore.collection("Users").whereEqualTo("email", user.getEmail())
                        .get().addOnSuccessListener(snapshots -> {
                    User currentUser = snapshots.getDocuments().get(0).toObject(User.class);

                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                            .replace(mainActivity.getFrmContainer().getId(),
                                    new NewFeedFragment(currentUser))
                            .addToBackStack("new_feeds_ui")
                            .commit();
                });
            } else {
                mainActivity.setFragment(new LoginFragment());
            }
        }, 2 * 1000);

        return splashScreenView;
    }
}
