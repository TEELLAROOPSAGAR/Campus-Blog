package com.example.authapp;

import static com.karumi.dexter.Dexter.withActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authapp.Adapters.MediaAdapter;
import com.example.authapp.classes.ImageModifier;
import com.example.authapp.classes.User;
import com.example.authapp.classes.postUploadInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import id.zelory.compressor.Compressor;

public class Add_blog extends AppCompatActivity {

    int PICK_IMAGE_INTENT = 1;
    ArrayList<Uri> mediaUriList = new ArrayList<>();
    ArrayList<String> mediaUrlList = new ArrayList<>();
    ArrayList<Bitmap> mediaBitmapList = new ArrayList<>();
    File newImageFile;
    File file;
    private FrameLayout progressBarContainer;
    private EditText post_title,post_des;
    private Button  upload,choose_image;
    private TextView text_progress;
    private ProgressBar progress_loading;
    RecyclerView list;
    private RecyclerView.Adapter  mediaAdapter;
    private FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference blogDB;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    com.example.authapp.classes.postUploadInfo postUploadInfo = new postUploadInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);
        progressBarContainer = findViewById(R.id.progressBarContainer);
        progress_loading = (ProgressBar)findViewById(R.id.progress_loading);
        text_progress = (TextView)findViewById(R.id.text_progress);
        post_title = (EditText) findViewById(R.id.post_title);
        post_des = (EditText) findViewById(R.id.post_des);
        upload = (Button) findViewById(R.id.upload);
        choose_image = (Button) findViewById(R.id.choose_image);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        blogDB = FirebaseDatabase.getInstance().getReference("BlogsDUP");

        firebaseFirestore = FirebaseFirestore.getInstance();
        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
                mediaBitmapList.clear();
                mediaUriList.clear();

            }
        });
        initializeMedia();
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPost()){ //complete the function checkpost

                    progressBarContainer.setVisibility(View.VISIBLE);

//                        String tmpUri = mediaUriList.get(0).getPath().toString();
//                        Log.v("ImageUri",tmpUri);
                        storageReference = FirebaseStorage.getInstance().getReference();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                        String str = sdf.format(new Date());
                        StorageReference rf = storageReference.child("Blog_ImagesDUP/" + str + ".jpg");
                        Bitmap bitmap = null;
                        byte[] fileInBytes = null;
                    bitmap = mediaBitmapList.get(0);
                    try {
                        bitmap = new Compressor(Add_blog.this)
                                     .setMaxHeight(200)
                                     .setMaxWidth(200)
                                     .setQuality(100)
                                     .compressToBitmap(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                                Log.v("bitmap",bitmap.toString());
//                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mediaUriList.get(0));

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    fileInBytes = baos.toByteArray();


                    rf.putBytes(fileInBytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.isComplete()){
                                        rf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                progressBarContainer.setVisibility(View.INVISIBLE);
                                                mediaUrlList.add(uri.toString());
                                                Log.v("post",uri.toString());
                                                String uid = auth.getCurrentUser().getUid();
                                                databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        User user = snapshot.getValue(User.class);
                                                        Log.v("thumb", user.getProfileThumbUrl() + "\n" +postUploadInfo.getThumbUrl());

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(Add_blog.this);

                                                        builder.setTitle("Confirm");
                                                        builder.setMessage("Once uploaded it can't be edited");
                                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // Do nothing but close the dialog

                                                                postUploadInfo.setUid(auth.getCurrentUser().getUid());
                                                                postUploadInfo.setUserName(user.getUsername());
                                                                postUploadInfo.setTitle(post_title.getText().toString().trim());
                                                                postUploadInfo.setDescription(post_des.getText().toString().trim());
                                                                postUploadInfo.setImageURLs(mediaUrlList);
                                                                postUploadInfo.setThumbUrl(user.getProfileThumbUrl());
//                                                                postUploadInfo.setPostLikesCount("0");
                                                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                                                                Date date = new Date();
//                                                                Date date = dateFormat.getCalendar().getTime();
                                                                 Calendar calendar = Calendar.getInstance();
                                                                SimpleDateFormat simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                                String date_str  = simpledateformat.format(calendar.getTime());
                                                                Date date = null;
                                                                try {
                                                                    date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(date_str);
                                                                } catch (ParseException e) {
                                                                    e.printStackTrace();
                                                                }
//                                                                dateFormat.format(date);
                                                                postUploadInfo.setTimeStamp(date);
//                                                        Log.v("testpr",user.getProfileThumbUrl());
                                                                //Log.v("post", postUploadInfo.title+" "+postUploadInfo.description + " " + postUploadInfo.getThumbUrl() + " " + postUploadInfo.imageURLs.get(0));
                                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                                                                String str = sdf.format(new Date());

                                                                firebaseFirestore.collection("BlogsDUP").document(str).set(postUploadInfo)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){
                                                                                      startActivity(new Intent(Add_blog.this,HomeScreen.class));
                                                                                      finish();
                                                                                  }else{
                                                                            Toast.makeText(Add_blog.this, "Upload Error please check!!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
//

//                                                dialog.dismiss();
                                                            }
                                                        });

                                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                // Do nothing
                                                                post_des.setEnabled(true);
                                                                post_title.setEnabled(true);
                                                                upload.setEnabled(true);
                                                                choose_image.setEnabled(true);
                                                                dialog.dismiss();
                                                            }
                                                        });

                                                        AlertDialog alert = builder.create();
                                                        alert.show();

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }
                        });

                    }
                }
        });
    }

    boolean checkPost(){
        if(post_title.getText().toString().isEmpty()){
            post_title.setError("field can't be empty");
            post_title.requestFocus();
            return false;
        }
        if(post_des.getText().toString().isEmpty()){
            post_des.setError("field can't be empty");
            post_des.requestFocus();
            return false;
        }
        if(mediaBitmapList.isEmpty()){
            Toast.makeText(this, "Please select an Image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initializeMedia() {
        list = findViewById(R.id.list);
        mediaBitmapList = new ArrayList<>();
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 1);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        list.setLayoutManager(linearLayoutManager);
        mediaAdapter = new MediaAdapter(Add_blog.this,mediaBitmapList);
        list.setAdapter(mediaAdapter);
    }
    private void openGallery() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
////        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setAction(intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_INTENT);

        withActivity(Add_blog.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent,"Select Your Image"),1);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }

                }).check();
    }

    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE_INTENT){
                if(data.getClipData() == null){
                    mediaUriList.add(data.getData());
//                    Log.v("medialistsize", mediaUriList.size() + "");
                }else{
                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
                        Uri dummy_uri = data.getClipData().getItemAt(i).getUri();
                        try {

                            // Getting selected image into Bitmap.
                             file = new File(getImagePath(dummy_uri));
                            Bitmap bitmap_dup = new Compressor(Add_blog.this)
                                    .setMaxHeight(200)
                                    .setMaxWidth(200)
                                    .setQuality(100)
                                    .compressToBitmap(file);
//                            Bitmap bitmap_dup = MediaStore.Images.Media.getBitmap(getContentResolver(), dummy_uri);
                            ImageModifier imageModifier = new ImageModifier();
                            mediaBitmapList.add(imageModifier.modifyOrientation(bitmap_dup,file.getAbsolutePath()));
                            // Setting up bitmap selected image into ImageView.

                            // After selecting image change choose button above text.

                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                        newImageFile = new File(data.getClipData().getItemAt(i).getUri().getPath());
                        Log.v("ImagePath",newImageFile.toString());
                    }
                }
                mediaAdapter.notifyDataSetChanged();
            }
        }
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
}