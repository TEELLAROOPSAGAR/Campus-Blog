package com.example.authapp.classes;


import android.util.Log;

public class User extends com.example.authapp.classes.GetUserId {

    public String name,email;
    public String branch;
    public String username;
    public String profileThumbUrl;

    public String getProfileThumbUrl() {
        return profileThumbUrl;
    }

    public void setProfileThumbUrl(String profileThumbUrl) {
        this.profileThumbUrl = profileThumbUrl;
        Log.v("pthumburi",profileThumbUrl);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
