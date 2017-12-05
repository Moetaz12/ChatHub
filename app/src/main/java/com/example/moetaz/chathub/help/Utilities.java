package com.example.moetaz.chathub.help;

import android.content.Context;

import com.example.moetaz.chathub.SharedPref;
import com.google.firebase.auth.FirebaseAuth;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import static com.example.moetaz.chathub.help.FirebaseConstants.FB_ROOT;

/**
 * Created by Moetaz on 3/23/2017.
 */

public class Utilities {


    public static String getUserId(){
        try {
           return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Exception e) {
            return "none";
        }
    }

    public static void getUserName(final Context context){
        final String[] name = new String[1];
        Firebase mUsers;
        mUsers = new Firebase(FB_ROOT+getUserId());
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
