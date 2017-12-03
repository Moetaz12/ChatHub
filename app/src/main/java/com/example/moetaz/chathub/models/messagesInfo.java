package com.example.moetaz.chathub.models;

/**
 * Created by Moetaz on 12/2/2017.
 */

public class messagesInfo {
    private String name;
    private String msg;
    private String sender;
    private String userName;

    public messagesInfo(){

    }

    public messagesInfo(String name, String msg, String sender, String userName) {
        this.name = name;
        this.msg = msg;
        this.sender = sender;
        this.userName = userName;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {

        return msg;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {

        return sender;
    }



    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String getUserName() {

        return userName;
    }
}
