package com.example.moetaz.chathub;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Moetaz on 3/23/2017.
 */

public class GlobalVariables {


    public static String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}
