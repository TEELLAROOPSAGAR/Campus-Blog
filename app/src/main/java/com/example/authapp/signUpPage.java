package com.example.authapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authapp.utils.StringManipulation;
import com.example.authapp.utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class signUpPage extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private TextView txtVHead;
    private EditText txtEName,txtEAge,txtEEmail,txtEPassword,txtEUserName,txtEPhone,txtECollege,txtEBranch;
    private Button btnSignUp;
    public boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signuppage);

        mAuth = FirebaseAuth.getInstance();
        txtVHead = (TextView)findViewById(R.id.txtVHead);
        txtEPassword = (EditText)findViewById(R.id.txtEPassword);
        txtEName = (EditText)findViewById(R.id.txtEName);
        txtEAge = (EditText)findViewById(R.id.txtEAge);
        txtEEmail=  (EditText)findViewById(R.id.txtEEmail);
        txtEUserName = (EditText)findViewById(R.id.txtEUserName);
        txtEPhone = (EditText)findViewById(R.id.txtEPhone);
        txtECollege = (EditText)findViewById(R.id.txtECollege);
        txtEBranch=  (EditText)findViewById(R.id.txtEBranch);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        txtVHead.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);


    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.txtVHead:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.btnSignUp:
                CheckUserDetails();
                break;

        }
    }

    private void CheckUserDetails() {
        check = true;
        String email = txtEEmail.getText().toString().trim();
        String password = txtEPassword.getText().toString().trim();
        String age = txtEAge.getText().toString().trim();
        String name = txtEName.getText().toString().trim();
        String username = txtEUserName.getText().toString().trim();
        String college = txtECollege.getText().toString().trim();
        String branch = txtEBranch.getText().toString().trim();
        String phone = txtEPhone.getText().toString().trim();

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

        if(password.length()<6){
            txtEPassword.setError("Minimum length should be 6");
            txtEPassword.requestFocus();
            return;
        }



        if(college.isEmpty()){
            txtECollege.setError("This field can't be empty");
            txtECollege.requestFocus();
            return;
        }
        if(branch.isEmpty()){
            txtEBranch.setError("This field can't be empty");
            txtEBranch.requestFocus();
            return;
        }
        if(age.isEmpty()){
            txtEAge.setError("Age is required");
            txtEAge.requestFocus();
            return;
        }
        if(phone.isEmpty()){
            txtEPhone.setError("This field can't be empty");
            txtEPhone.requestFocus();
            return;
        }
        if(phone.length()!=10){
            txtEPhone.setError("Enter  a valid Number");
            txtEName.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User();
                            user.setAge(age);
                            user.setEmail(email);
                            user.setName(name);
                            user.setUsername(username);
                            user.setBranch(branch);
                            user.setPhone(phone);
                            user.setCollege(college);
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

}