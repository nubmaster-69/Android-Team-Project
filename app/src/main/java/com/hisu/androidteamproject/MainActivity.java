package com.hisu.androidteamproject;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frmContainer;

    public FrameLayout getFrmContainer() {
        return frmContainer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frmContainer = findViewById(R.id.frmFragmentContainer);

        setFragment(new LoginFragment());
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(frmContainer.getId(), fragment)
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .commit();
    }
}