package com.example.moetaz.chathub.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.ui.fragments.ConversationFragment;

public class ConversationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.fconv,new ConversationFragment()).commit();
        }
    }
}
