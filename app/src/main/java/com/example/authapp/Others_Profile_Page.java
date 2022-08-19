package com.example.authapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import com.example.authapp.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Others_Profile_Page extends AppCompatActivity {

    private TextView name,email,age,username,college,branch,phone;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Dialog dialog;
    private User user;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile_page);

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

        String userName = "username not set";
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            username.setText("username  : "+extras.getString("username"));
            name.setText("Name   : "+extras.getString("name"));
            college.setText("college : "+extras.getString("college"));
            branch.setText("branch  : "+extras.getString("branch"));
            email.setText("email    : "+extras.getString("email"));
            phone.setText("phone   : "+extras.getString("phone"));
            age.setText("age        : "+extras.getString("age"));
        }

    }
}