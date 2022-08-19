package com.example.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgot_password extends AppCompatActivity{

    private EditText txtEEmail;
    private Button btnResetButton;
    private ProgressBar progressbar;
    private TextView txtVHead;
    private FirebaseAuth nAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        nAuth = FirebaseAuth.getInstance();

        txtEEmail = (EditText) findViewById(R.id.txtEEmail);
        progressbar = (ProgressBar)findViewById(R.id.progressbar);
        txtVHead = (TextView)findViewById(R.id.txtVHead);
        txtVHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(forgot_password.this,MainActivity.class));
            }
        });
        btnResetButton = (Button)findViewById(R.id.btnResetPassword);
        btnResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButton();
            }
        });

    }
    private void resetButton(){
        String email = txtEEmail.getText().toString().trim();
        if(email.isEmpty()){
            txtEEmail.setError("email can't be empty!");
            txtEEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEEmail.setError("Enter a valid email address");
            txtEEmail.requestFocus();
            return;
        }
        progressbar.setVisibility(View.VISIBLE);

        nAuth.sendPasswordResetEmail(email).addOnCompleteListener(new  OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(forgot_password.this,"Reset password email has been sent successfully!",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(forgot_password.this,"Sorry!Request failed!",Toast.LENGTH_LONG).show();
                }
            }
        });
        progressbar.setVisibility(View.INVISIBLE);
    }

}