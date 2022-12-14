package com.example.authapp.classes;

import com.google.firebase.firestore.Exclude;

import io.reactivex.annotations.NonNull;

public class GetUserId {

    @Exclude
    public  String GetUserId;

    public <T extends GetUserId> T withId(@NonNull final String id){
        this.GetUserId = id;
        return (T) this;
    }
}
