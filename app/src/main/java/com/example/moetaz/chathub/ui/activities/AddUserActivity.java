package com.example.moetaz.chathub.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.ui.fragments.AddUserFragment;

public class AddUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.frame_adduser,new AddUserFragment())
                    .commit();
        }
    }
}
