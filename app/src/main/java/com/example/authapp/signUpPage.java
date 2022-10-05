package com.example.authapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authapp.utils.StringManipulation;
import com.example.authapp.utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class signUpPage extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private TextView txtVHead;
    private EditText txtEName,txtEEmail,txtEPassword,txtEConfirmPassword,txtEUserName,txtEBranch;
    private Button btnSignUp;
    private CircleImageView profileImage;
    public boolean check = true;
    int Image_Request_Code = 1;
    Uri FilePathUri;
    Bitmap bitmap;
    byte[] fileInBytes;

    StorageReference storageReference;

    String uu="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signuppage);

        mAuth = FirebaseAuth.getInstance();
        txtVHead = (TextView)findViewById(R.id.txtVHead);
        txtEPassword = (EditText)findViewById(R.id.txtEPassword);
        txtEConfirmPassword = (EditText)findViewById(R.id.txtEConfirmPassword);
        txtEName = (EditText)findViewById(R.id.txtEName);
        txtEEmail=  (EditText)findViewById(R.id.txtEEmail);
        txtEUserName = (EditText)findViewById(R.id.txtEUserName);
        txtEBranch=  (EditText)findViewById(R.id.txtEBranch);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        profileImage = (CircleImageView)findViewById(R.id.ProfileImage);

        txtVHead.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImg(view);

            }
        });
        storageReference = FirebaseStorage.getInstance().getReference();
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.txtVHead:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.btnSignUp:
                UploadImageFileToFirebaseStorage(fileInBytes);
                CheckUserDetails();

                break;

        }
    }

    private void CheckUserDetails() {
        check = true;
        String email = txtEEmail.getText().toString().trim();
        String password = txtEPassword.getText().toString().trim();
        String confirmPassword = txtEConfirmPassword.getText().toString().trim();
        String name = txtEName.getText().toString().trim();
        String username = txtEUserName.getText().toString().trim();
        String branch = txtEBranch.getText().toString().trim();

        DataSnapshot dataSnapshot = null;
        if(name.isEmpty()){
            txtEName.setError("Name is required");
            txtEName.requestFocus();
            return;
        }

        if(username.isEmpty()){
            txtEUserName.setError("This field can't be empty");
            txtEUserName.requestFocus();
            return;
        }

        //This is to check if username already exists but not working
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = new User();
                    Log.d(TAG,"checkIfUserNameExists: dataSnapshot: "+ds);
                    user.setUsername(ds.getValue(User.class).getUsername());
                    Log.d(TAG,"checkIfUserNameExists: username: "+user.getUsername());

                    if(StringManipulation.expandUserName(user.getUsername()).equals(username)){
                        Log.d(TAG,"checkIfUserNameExists: Found a match: "+user.getUsername());
                        check = false;
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(signUpPage.this,"Something Wrong for username!",Toast.LENGTH_LONG).show();
            }
        });

        if(check == false){
            txtEUserName.setError("Username already exists");
            txtEUserName.requestFocus();
            return;
        }

        if(email.isEmpty()){
            txtEEmail.setError("email is required");
            txtEEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEEmail.setError("Enter a valid email Address");
            txtEEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            txtEPassword.setError("Set a password");
            txtEPassword.requestFocus();
            return;
        }
        if(!password.equals(confirmPassword)){
            txtEConfirmPassword.setError("Password does not match", null);
            txtEConfirmPassword.requestFocus();
            return;
        }

        if(password.length()<6){
            txtEPassword.setError("Minimum length should be 6");
            txtEPassword.requestFocus();
            return;
        }

        if(branch.isEmpty()){
            txtEBranch.setError("This field can't be empty");
            txtEBranch.requestFocus();
            return;
        }



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
                            user.setProfileThumbUrl(uu);
                            Log.v("uu",uu);
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
                    }
                });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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

    public void UploadImageFileToFirebaseStorage(byte[] fileInBytes){


        StorageReference storageReference2nd = storageReference.child("Profile_Pics/" + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
        storageReference2nd.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        uu = imageUrl;
                        Log.v("pimg",uu);
//                        user.setProfileThumbUrl(imageUrl);
                        //createNewPost(imageUrl);
//                        String ImageUploadId = databaseReference.push().getKey();
//                        ImageUploadInfo imageUploadInfo = new ImageUploadInfo(TempImageName, imageUrl);
//
//
//                        databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                    }
                });
            }
        });

    }
}