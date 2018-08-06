package com.example.moetaz.chathub.models;

/**
 * Created by Moetaz on 12/2/2017.
 */

public class MessagesInfo {
    private String name;
    private String msg;
    private String sender;
    private String userName;
    private String profilePic;

    public MessagesInfo() {

    }

    public MessagesInfo(String name, String msg, String sender, String userName, String profilePic) {
        this.name = name;
        this.msg = msg;
        this.sender = sender;
        this.userName = userName;
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {

        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSender() {

        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getProfilePic() {

        return profilePic;
    }
}
