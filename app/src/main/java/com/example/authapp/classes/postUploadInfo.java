package com.example.authapp.classes;

import java.util.ArrayList;
import java.util.Date;

public class postUploadInfo extends com.example.authapp.classes.BlogPostId {

    public String title;
    public String uid;
    public String thumbUrl;
    public ArrayList<String> imageURLs = new ArrayList<>();
    public String description;
    public Date timeStamp;

//    public String getPostLikesCount() {
//        return postLikesCount;
//    }
//
//    public void setPostLikesCount(String postLikesCount) {
//        this.postLikesCount = postLikesCount;
//    }
//
//    public String postLikesCount;

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String userName;

    public postUploadInfo(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public ArrayList<String> getImageURLs() {
        return imageURLs;
    }

    public void setImageURLs(ArrayList<String> imageURLs) {
        this.imageURLs = imageURLs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
