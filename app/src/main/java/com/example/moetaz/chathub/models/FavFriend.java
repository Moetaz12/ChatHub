package com.example.moetaz.chathub.models;

import java.io.Serializable;

/**
 * Created by Moetaz on 12/10/2017.
 */

public class FavFriend implements Serializable {
    private String id;
    private String userName;

    public void setId(String id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {

        return id;
    }

    public String getUserName() {
        return userName;
    }
}
