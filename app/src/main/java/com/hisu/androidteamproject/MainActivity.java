package com.hisu.androidteamproject;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hisu.androidteamproject.entity.User;
import com.hisu.androidteamproject.fragment.LoginFragment;
import com.hisu.androidteamproject.fragment.NewFeedFragment;
import com.hisu.androidteamproject.fragment.SplashScreenFragment;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frmContainer;
    private FirebaseAuth auth;

    public FrameLayout getFrmContainer() {
        return frmContainer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        frmContainer = findViewById(R.id.frmFragmentContainer);

        setFragment(new SplashScreenFragment());
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(frmContainer.getId(), fragment)
                .commit();
    }

    private void clearFragmentListBeforeLogOut() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry topFragment = manager.getBackStackEntryAt(0);
            manager.popBackStack(topFragment.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void logOut() {
        if (auth != null) {
            auth.signOut();
            clearFragmentListBeforeLogOut();
            setFragment(new LoginFragment());
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1 )
            this.finish();
        else
            getSupportFragmentManager().popBackStack();
    }
}