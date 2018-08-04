package com.example.moetaz.chathub.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String imgeUrl;
    private String name;
    private String key;

    public User() {
    }

    protected User(Parcel in) {
        imgeUrl = in.readString();
        name = in.readString();
        key = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setImgeUrl(String imgeUrl) {
        this.imgeUrl = imgeUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgeUrl() {

        return imgeUrl;
    }

    public String getName() {
        return name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {

        return key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imgeUrl);
        dest.writeString(name);
        dest.writeString(key);
    }
}
