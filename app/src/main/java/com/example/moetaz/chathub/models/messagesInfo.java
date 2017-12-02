package com.example.moetaz.chathub.models;

/**
 * Created by Moetaz on 12/2/2017.
 */

public class messagesInfo {
    private String name;

    public messagesInfo(){

    }
    public messagesInfo(String name ) {
        this.name = name;

    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
