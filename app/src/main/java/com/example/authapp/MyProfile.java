package com.example.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authapp.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {

    private TextView name,email,username,branch;
    private CircleImageView MyProfileImg;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private User user;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        name=(TextView)findViewById(R.id.txtVName);
        email=(TextView)findViewById(R.id.txtVEmailAddress);
        username=(TextView)findViewById(R.id.txtVUserName);
        branch=(TextView)findViewById(R.id.txtVBranch);
        MyProfileImg = (CircleImageView)findViewById(R.id.MyProfileImg);
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        if(!uid.isEmpty()){
            getUserData();
        }

        FirebaseDatabase firebaseDatabase
                = FirebaseDatabase.getInstance();

        // we will get a DatabaseReference for the database
        // root node
        DatabaseReference databaseReference
                = firebaseDatabase.getReference();

        // Here "image" is the child node value we are
        // getting child node data in the getImage variable
        DatabaseReference getImage
                = databaseReference.child("profileThumbUrl");

        // Adding listener for a single change
        // in the data at this location.
        // this listener will triggered once
        // with the value of the data at the location
//        getImage.addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(
//                            @NonNull DataSnapshot dataSnapshot)
//                    {
//                        // getting a DataSnapshot for the
//                        // location at the specified relative
//                        // path and getting in the link variable
//                        String link = dataSnapshot.getValue(
//                                String.class);
//                        Log.v("link",link);
//
//                        // loading that data into rImage
//                        // variable which is ImageView
//                        Picasso.get().load(link).into(MyProfileImg);
//                    }
//
//                    // this will called when any problem
//                    // occurs in getting data
//                    @Override
//                    public void onCancelled(
//                            @NonNull DatabaseError databaseError)
//                    {
//                        // we are showing that error message in
//                        // toast
//                        Toast
//                                .makeText(MyProfile.this,
//                                        "Error Loading Image",
//                                        Toast.LENGTH_SHORT)
//                                .show();
//                    }
//                });
    }



    private void getUserData() {
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user=snapshot.getValue(User.class);
                username.setText("username  : "+user.getUsername());
                name.setText("Name   : "+user.getName());
                branch.setText("branch  : "+user.getBranch());
                email.setText("email    : "+user.getEmail());
//                MyProfileImg.setImageBitmap(getBitmapFromURL(user.getProfileThumbUrl()));
                //It's working but image is loading too slow.
//                Picasso.get().load(user.getProfileThumbUrl()).into(MyProfileImg);
                Log.v("profileimage",user.getProfileThumbUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
 
            }
        });
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
}