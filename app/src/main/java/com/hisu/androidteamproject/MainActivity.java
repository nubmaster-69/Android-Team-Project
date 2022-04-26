package com.hisu.androidteamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frmContainer = findViewById(R.id.frmFragmentContainer);

        getSupportFragmentManager()
                .beginTransaction()
                .add(frmContainer.getId(), new LoginFragment())
                .commit();
    }
}