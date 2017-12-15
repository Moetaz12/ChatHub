package com.example.moetaz.chathub.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.ui.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fmain, new MainFragment())
                    .commit();
        }
    }
}
