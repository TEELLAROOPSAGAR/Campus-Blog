package com.example.authapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.authapp.classes.ImageModifier;
import com.example.authapp.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class signUpPage extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    private EditText txtEName,txtEEmail,txtEPassword,txtEConfirmPassword,txtEUserName,txtEBranch;
    private TextView go_to_login;
    private Button btnSignUp;
    private ProgressBar progress_signUp;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private CircleImageView profileImage;
    private String mImageFileLocation;
    public boolean check = false;
    int Image_Request_Code = 1;
    Uri FilePathUri;
    Bitmap bitmap;
    byte[] fileInBytes;
    String email;
    String password;
    String confirmPassword;
    String name;
    String username;
    String branch;

    StorageReference storageReference;

    String uu="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signuppage);

        mAuth = FirebaseAuth.getInstance();
        txtEPassword = findViewById(R.id.txtEPassword);
        txtEConfirmPassword = (EditText)findViewById(R.id.txtEConfirmPassword);
        txtEName = (EditText)findViewById(R.id.txtEName);
        txtEEmail=  (EditText)findViewById(R.id.txtEEmail);
        txtEUserName = (EditText)findViewById(R.id.txtEUserName);
        go_to_login = (TextView)findViewById(R.id.go_to_login);
        txtEBranch=  (EditText)findViewById(R.id.txtEBranch);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        profileImage = (CircleImageView)findViewById(R.id.ProfileImage);
        progress_signUp = (ProgressBar)findViewById(R.id.progress_signUp);

        go_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signUpPage.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckUserDetails()){
                    UploadImageFileToFirebaseStorage(fileInBytes);
                }
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPermissions(signUpPage.this)){
//                    pickImage(signUpPage.this);
                    selectImg(view);
                }
//
            }

        });
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private boolean CheckUserDetails() {
        check = true;
         email = txtEEmail.getText().toString().trim();
         password = txtEPassword.getText().toString().trim();
         confirmPassword = txtEConfirmPassword.getText().toString().trim();
         name = txtEName.getText().toString().trim();
        username = txtEUserName.getText().toString().trim();
        branch = txtEBranch.getText().toString().trim();

        DataSnapshot dataSnapshot = null;
        if(name.isEmpty()){
            txtEName.setError("Name is required");
            txtEName.requestFocus();
            return false;
        }

        if(username.isEmpty()){
            txtEUserName.setError("This field can't be empty");
            txtEUserName.requestFocus();
            return false;
        }

        if(email.isEmpty()){
            txtEEmail.setError("email is required");
            txtEEmail.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEEmail.setError("Enter a valid email Address");
            txtEEmail.requestFocus();
            return false;
        }
        if(!email.endsWith("iitism.ac.in")){
            txtEEmail.setError("Enter your Institute email Id");
            txtEEmail.requestFocus();
            return false;
        }
        if(password.isEmpty()){
            txtEPassword.setError("Set a password");
            txtEPassword.requestFocus();
            return false;
        }
        if(!password.equals(confirmPassword)){
            txtEConfirmPassword.setError("Password does not match", null);
            txtEConfirmPassword.requestFocus();
            return false;
        }

        if(password.length()<6){
            txtEPassword.setError("Minimum length should be 6");
            txtEPassword.requestFocus();
            return false;
        }

        if(branch.isEmpty()){
            txtEBranch.setError("This field can't be empty");
            txtEBranch.requestFocus();
            return false;
        }
        //implement for profile image
//        if(check){
//            Toast.makeText(this, "Please select your profile photo", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }
//this was too to check single username
//    public static boolean checkIfUserNameExits(String username, DataSnapshot dataSnapshot){
//        Log.d(TAG,"checkIfUserNameExists : checking if "+username+"already exists.");
//        User user = new User();
//        for(DataSnapshot ds:dataSnapshot.getChildren()){
//            Log.d(TAG,"checkIfUserNameExists: dataSnapshot: "+ds);
//            user.setUsername(ds.getValue(User.class).getUsername());
//            Log.d(TAG,"checkIfUserNameExists: username: "+user.getUsername());
//
//            if(StringManipulation.expandUserName(user.getUsername()).equals(username)){
//                Log.d(TAG,"checkIfUserNameExists: Found a match: "+user.getUsername());
//
//                return true;
//            }
//        }
//        return false;
//    }




    public void selectImg(View view) {
        Dexter.withActivity(signUpPage.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
//                        intent.setType("video/*");
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
//    @Override
//    public void onBackPressed(){
//        Intent a = new Intent(signUpPage.this,MainActivity.class);
//        startActivity(a);
//    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
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
        System.out.println(path);


        return path;
    }

    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
    private void pickImage(Context context) {
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery"}; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (optionsMenu[i].equals("Take Photo")) {
                    // Open the camera and get the photo
//                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(takePicture, 0);
                    Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent1, 0);
                } else if (optionsMenu[i].equals("Choose from Gallery")) {
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        check = false;
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("rcode", requestCode + "");
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
//
                        bitmap = (Bitmap) data.getExtras().get("data");
//                        FilePathUri = getImageUri(getApplicationContext(),bitmap);
                        FilePathUri = data.getData();
                        Log.v("fileuRI",FilePathUri.toString());
                        try {

                            // Getting selected image into Bitmap.
                            File file = new File(getRealPathFromURI(FilePathUri));
                            Bitmap bitmap_dup = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                            ImageModifier imageModifier = new ImageModifier();
                            bitmap = imageModifier.modifyOrientation(bitmap_dup,file.getAbsolutePath());
                            // Setting up bitmap selected image into ImageView.
                            profileImage.setImageBitmap(bitmap);
                            check = true;
                            // After selecting image change choose button above text.

                        } catch (IOException e) {

                            e.printStackTrace();
                        }

                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null && data.getData() != null) {
//
                        FilePathUri = data.getData();

                        try {

                            // Getting selected image into Bitmap.
                            File file = new File(getRealPathFromURI(FilePathUri));
                            System.out.println(FilePathUri);
                            Bitmap bitmap_dup = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                            ImageModifier imageModifier = new ImageModifier();
                            bitmap = imageModifier.modifyOrientation(bitmap_dup,file.getAbsolutePath());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            fileInBytes = baos.toByteArray();
                            // Setting up bitmap selected image into ImageView.
                            profileImage.setImageBitmap(bitmap);
                            check = true;
                            // After selecting image change choose button above text.

                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

        public void UploadImageFileToFirebaseStorage ( byte[] fileInBytes) {
            if(FilePathUri==null){
                Toast.makeText(signUpPage.this,"please set a profile photo to continue",Toast.LENGTH_SHORT).show();
                return;
            }


            progress_signUp.setVisibility(View.VISIBLE);
            StorageReference storageReference2nd = storageReference.child("Profile_Pics /" + System.currentTimeMillis() + ".jpg");

            storageReference2nd.putBytes(fileInBytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.isComplete()) {
                            storageReference2nd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    Log.v("pimg", imageUrl);

                                    mAuth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {


                                                    if (task.isSuccessful()) {
                                                        User user = new User();
                                                        user.setEmail(email);
                                                        user.setName(name);
                                                        user.setUsername(username);
                                                        user.setBranch(branch);
                                                        user.setProfileThumbUrl(imageUrl);
                                                        Log.v("uu", user.getProfileThumbUrl());

//                                                        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user)
//                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                            @Override
//                                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                                if (task.isSuccessful()) {
//                                                                                    Toast.makeText(signUpPage.this, "Registered Successfully", Toast.LENGTH_LONG).show();
//                                                                                } else {
//                                                                                    Toast.makeText(signUpPage.this, "failed to register! Try again!", Toast.LENGTH_LONG).show();
//                                                                                }
//                                                                            }
//                                                                        });


                                                        FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {

                                                                            Toast.makeText(signUpPage.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                                                        } else {
                                                                            Toast.makeText(signUpPage.this, "failed to register! Try again!", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(signUpPage.this, "failed to register! Try again!", Toast.LENGTH_LONG).show();
                                                    }
                                                    progress_signUp.setVisibility(View.INVISIBLE);
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