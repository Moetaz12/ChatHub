package com.example.moetaz.chathub;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by Moetaz on 3/23/2017.
 */

public class Utilities {


    public static String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static void getUserName(final Context context){
        final String[] name = new String[1];
        Firebase mUsers;
        mUsers = new Firebase("https://chathub-635f9.firebaseio.com/usersinfo/"+getUserId());
        mUsers.child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                  name[0] = (String)dataSnapshot.getValue();
               new SharedPref(context).SaveItem("UserName",name[0]);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

}
