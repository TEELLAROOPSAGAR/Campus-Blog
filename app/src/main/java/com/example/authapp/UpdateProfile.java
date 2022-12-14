package com.example.authapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateProfile extends AppCompatActivity {

    private EditText newName,newUserName,newBranch,newEmail;
    private Button btnCancel,btnUpdate;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        newName = findViewById(R.id.newName);
        newUserName = findViewById(R.id.newUserName);
        newBranch = findViewById(R.id.newBranch);
//        newEmail = findViewById(R.id.newEmail);
        btnCancel = findViewById(R.id.btnCancel);
        btnUpdate = findViewById(R.id.btnUpdate);

        Bundle extras = getIntent().getExtras();
        newName.setText(extras.getString("name"));
        newUserName.setText(extras.getString("username"));
        newBranch.setText(extras.getString("branch"));
//        newEmail.setText(extras.getString("email"));

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(UpdateProfile.this,MyProfile.class);
//                startActivity(intent);
                finish();

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Toast.makeText(this,"Updated Successfully",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfile.this);

                    builder.setTitle("Update");
                    builder.setMessage("Click confirm to update");
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            updateDetails();
                            Toast.makeText(UpdateProfile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void updateDetails() {
       databaseReference.child("name").setValue(newName.getText().toString());
        databaseReference.child("username").setValue(newUserName.getText().toString());
//        databaseReference.child("email").setValue(newEmail.getText().toString());
        databaseReference.child("branch").setValue(newBranch.getText().toString());
        return ;

    }

}