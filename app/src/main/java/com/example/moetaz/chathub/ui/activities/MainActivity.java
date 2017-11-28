package com.example.moetaz.chathub.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.moetaz.chathub.ui.fragments.MainFragment;
import com.example.moetaz.chathub.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.fmain,new MainFragment())
                    .commit();
        }
    }
}
