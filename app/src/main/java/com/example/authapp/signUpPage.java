package com.example.authapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class signUpPage extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    private EditText txtEName,txtEEmail,txtEPassword,txtEConfirmPassword,txtEUserName,txtEBranch;
    private TextView go_to_login;
    private Button btnSignUp;
    private ProgressBar progress_signUp;
    private CircleImageView profileImage;
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
                selectImg(view);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        check = false;
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("rcode",requestCode+"");
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                fileInBytes = baos.toByteArray();
                // Setting up bitmap selected image into ImageView.
                profileImage.setImageBitmap(bitmap);
               check = true;
                // After selecting image change choose button above text.

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }



        public void UploadImageFileToFirebaseStorage ( byte[] fileInBytes) {

            progress_signUp.setVisibility(View.VISIBLE);

            StorageReference storageReference2nd = storageReference.child("Profile_Pics /" + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

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