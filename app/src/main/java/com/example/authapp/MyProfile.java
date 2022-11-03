package com.example.authapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.authapp.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {

    private TextView name,email,username,branch;
    private CircleImageView MyProfileImg;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private User user;
    private String uid;
    private ImageView editProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        name=(TextView)findViewById(R.id.txtVName);
        email=(TextView)findViewById(R.id.txtVEmailAddress);
        username=(TextView)findViewById(R.id.txtVUserName);
        branch=(TextView)findViewById(R.id.txtVBranch);
        MyProfileImg = (CircleImageView)findViewById(R.id.MyProfileImg);
        editProfile = findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_to_editProfile();
            }
        });
        auth = FirebaseAuth.getInstance();
//        uid = auth.getCurrentUser().getUid().toString();
        Bundle extras = getIntent().getExtras();
         uid = extras.getString("User_id");
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

    private void go_to_editProfile() {
        Intent intent = new Intent(MyProfile.this,UpdateProfile.class);
        intent.putExtra("username",username.getText().toString());
        intent.putExtra("name",name.getText().toString());
        intent.putExtra("branch",branch.getText().toString());
        intent.putExtra("email",email.getText().toString());
        startActivity(intent);
    }


    private void getUserData() {
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user=snapshot.getValue(User.class);
                username.setText(user.getUsername());
                name.setText(user.getName());
                branch.setText(user.getBranch());
                email.setText(user.getEmail());
//                MyProfileImg.setImageBitmap(getBitmapFromURL(user.getProfileThumbUrl()));
                //It's working but image is loading too slow.
                Picasso.get().load(user.getProfileThumbUrl()).into(MyProfileImg);
                Log.v("profileimage",user.getProfileThumbUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
 
            }
        });
    }

}