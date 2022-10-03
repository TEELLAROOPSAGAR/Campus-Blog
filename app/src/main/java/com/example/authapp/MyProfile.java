package com.example.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.widget.TextView;

import com.example.authapp.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfile extends AppCompatActivity {

    private TextView name,email,age,username,college,branch,phone;
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
        age=(TextView)findViewById(R.id.txtVAge);
        username=(TextView)findViewById(R.id.txtVUserName);
        college=(TextView)findViewById(R.id.txtVCollege);
        branch=(TextView)findViewById(R.id.txtVBranch);
        phone=(TextView)findViewById(R.id.txtVPhone);

        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        if(!uid.isEmpty()){
            getUserData();
        }
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
 
            }
        });
    }
}