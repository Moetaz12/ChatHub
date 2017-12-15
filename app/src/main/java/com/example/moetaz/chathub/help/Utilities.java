package com.example.moetaz.chathub.help;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.dataStorage.SharedPref;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.moetaz.chathub.help.FirebaseConstants.FB_ROOT;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERNAME_NODE;

/**
 * Created by Moetaz on 3/23/2017.
 */

public class Utilities {


    public static String getUserId() {
        try {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Exception e) {
            return "none";
        }
    }

    public static void saveUserName(final Context context) {
        final String[] name = new String[1];
        Firebase mUsers;
        mUsers = new Firebase(FB_ROOT + getUserId());
        mUsers.child(USERNAME_NODE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name[0] = (String) dataSnapshot.getValue();
                new SharedPref(context).SaveItem(context.getString(R.string.usrename_pref), name[0]);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public static String getUserName(Context context) {
        return new SharedPref(context).GetItem(context.getString(R.string.usrename_pref));
    }

    public static void message(Context context, String m) {
        Toast.makeText(context, m, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);

    }
}
