package com.example.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.authapp.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//import Add_post.java;


public class Add_blog extends AppCompatActivity {

    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();
    private EditText post_title,post_des;
    private Button  upload,choose_image;
    RecyclerView list;
    private RecyclerView.Adapter  mediaAdapter;
    ArrayList<Uri> ImageList = new ArrayList<>();
//    Uri u = com.example.authapp.Add_post.FilePathUri;
    private FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference blogDB;
    PostUploadInfo postUploadInfo = new PostUploadInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);

        post_title = (EditText) findViewById(R.id.post_title);
        post_des = (EditText) findViewById(R.id.post_des);
        upload = (Button) findViewById(R.id.upload);
        choose_image = (Button) findViewById(R.id.choose_image);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        blogDB = FirebaseDatabase.getInstance().getReference("Blogs");
        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        initializeMedia();
//        final ListView list = findViewById(R.id.list);
//        list.setRotation(90);


//        String s = "https://firebasestorage.googleapis.com/v0/b/authapp-51103.appspot.com/o/Profile_Pics%2F1664799738266.jpg?alt=media&token=7a7fbb90-597f-4ee9-9320-460cec8ae12a";
//        try {
//            Uri u = new Uri(s);
////            Uri u = Uri.parse("content://com.android.providers.media.documents/document/image%3A8194");
//
////            URI u = new URI("https://firebasestorage.googleapis.com/v0/b/authapp-51103.appspot.com/o/Profile_Pics%2F1664799738266.jpg?alt=media&token=7a7fbb90-597f-4ee9-9320-460cec8ae12a");
//            ImageList.add(u);
//            ImageList.add(u);
//            ImageList.add(u);
//            ImageList.add(u);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Uri u = Uri.parse("https://firebasestorage.googleapis.com/v0/b/authapp-51103.appspot.com/o/Profile_Pics%2F1664799738266.jpg?alt=media&token=7a7fbb90-597f-4ee9-9320-460cec8ae12a");
//        CustomAdapter customAdapter = new CustomAdapter(this, ImageList);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPost()){ //complete the function checkpost

                    postUploadInfo.setTitle(post_title.getText().toString().trim());
                    postUploadInfo.setDescription(post_des.getText().toString().trim());
                    postUploadInfo.setImageURLs(mediaUriList);

                    getThumbUrl(); //this is for getting profile image

//                    int s = 0;
//                    for(int i=0;i<100000;i++){
//                        for(int j=0;j<10000;j++){
//                            s = (s + 10)%77;
//                        }
//                    }
                    Log.v("post", postUploadInfo.title+" "+postUploadInfo.description + " " + postUploadInfo.getThumbUrl() + " " + postUploadInfo.imageURLs.get(0));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    String str = sdf.format(new Date());
                    blogDB.child(auth.getCurrentUser().getUid())
                            .child(str).setValue(postUploadInfo);
                }

            }
        });
    }

    boolean checkPost(){
        return true;
    }

    private void getThumbUrl() {
        String uid = auth.getCurrentUser().getUid();
        databaseReference.child(uid).child("profileThumbUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                User user = new User();
//                user=snapshot.getValue(User.class);

                String thumbUrl = snapshot.getValue().toString();
                postUploadInfo.setThumbUrl(thumbUrl);
                Log.v("thumb", thumbUrl + "\n" +postUploadInfo.getThumbUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initializeMedia() {
        list = findViewById(R.id.list);
        mediaUriList = new ArrayList<>();
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        list.setLayoutManager(linearLayoutManager);
        mediaAdapter = new MediaAdapter(Add_blog.this,mediaUriList);
        list.setAdapter(mediaAdapter);
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE_INTENT){
                if(data.getClipData() == null){
                    mediaUriList.add(data.getData().toString());
//                    Log.v("medialistsize", mediaUriList.size() + "");
                }else{
                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }
                mediaAdapter.notifyDataSetChanged();
            }
        }
    }

}