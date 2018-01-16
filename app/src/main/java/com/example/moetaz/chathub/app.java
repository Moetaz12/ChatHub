package com.example.moetaz.chathub;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Moetaz on 12/19/2017.
 */

public class app extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
