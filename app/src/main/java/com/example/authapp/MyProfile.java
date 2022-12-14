package com.example.authapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authapp.Adapters.BlogRecyclerAdapter;
import com.example.authapp.classes.User;
import com.example.authapp.classes.postUploadInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {

    private TextView name,email,username,branch;
    private CircleImageView MyProfileImg;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ArrayList<postUploadInfo> personal_blog_list;
    private RecyclerView personal_recycler;
    private User user;
    private String uid;
    private ImageView editProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        personal_recycler = findViewById(R.id.personal_Posts);
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
        Bundle extras = getIntent().getExtras();
         uid = extras.getString("User_id");

         if(uid.equals(auth.getCurrentUser().getUid())){
             editProfile.setVisibility(View.VISIBLE);
         }
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

        LoadAllPersonalPosts();
    }

    private void LoadAllPersonalPosts() {
        personal_blog_list = new ArrayList<>();
        BlogRecyclerAdapter blogRecyclerAdapter = new BlogRecyclerAdapter(personal_blog_list,MyProfile.this);

        personal_recycler.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore.getInstance().collection("BlogsDUP").whereEqualTo("uid",uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        try{
                            for(DocumentChange documentChange : value.getDocumentChanges()){
                                if(documentChange.getType()==DocumentChange.Type.ADDED){
                                    String Blogpost_id = documentChange.getDocument().getId();
                                    postUploadInfo post  = documentChange.getDocument().toObject(postUploadInfo.class).withId(Blogpost_id);
                                    personal_blog_list.add(0,post);
                                    blogRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        }catch (Exception exception){
                            Toast.makeText(MyProfile.this,"Loading error please refresh\n or start again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        personal_recycler.setAdapter(blogRecyclerAdapter);
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
                if(snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                    username.setText(user.getUsername());
                    name.setText(user.getName());
                    branch.setText(user.getBranch());
                    email.setText(user.getEmail());
//                MyProfileImg.setImageBitmap(getBitmapFromURL(user.getProfileThumbUrl()));
                    //It's working but image is loading too slow.
                    Picasso.get().load(user.getProfileThumbUrl()).into(MyProfileImg);
                    Log.v("profileimage", user.getProfileThumbUrl());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
 
            }
        });
    }
}