package com.example.authapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.authapp.jsonFiles.OpenJSON;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class preSignUpPage extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener{

    private TextInputEditText txtEFirstName;
    private TextInputEditText txtELastName;
    private TextInputEditText txtEUserName;
    private TextInputEditText txtEEmail;
    private TextInputEditText txtEPassword;
    private TextInputEditText txtEConfirmPassword;
    private FloatingActionButton btnNext;

    private Spinner spnState, spnDistrict;

    private ArrayList<String> states;
    private ArrayList<String> arrayDistricts;


    //JSON Array
    private JSONArray result;
    JSONObject j = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_sign_up_page);

        //Initializing the ArrayList
        states = new ArrayList<String>();
        arrayDistricts = new ArrayList<String>();

        txtEFirstName = findViewById(R.id.txtEFirstName);
        txtELastName = findViewById(R.id.txtELastName);
        txtEUserName = findViewById(R.id.txtEUserName);
        txtEEmail = findViewById(R.id.txtEEmail);
        txtEPassword = findViewById(R.id.txtEPassword);
        txtEConfirmPassword = findViewById(R.id.txtEConfirmPassword);
        btnNext = findViewById((R.id.btnNext));

        btnNext.setOnClickListener(this);
        txtELastName.setOnFocusChangeListener(this);
        txtEUserName.setOnFocusChangeListener(this);
        txtEEmail.setOnFocusChangeListener(this);
        txtEPassword.setOnFocusChangeListener(this);
        txtEConfirmPassword.setOnFocusChangeListener(this);

        spnState = (Spinner) findViewById(R.id.spnState);
        spnDistrict = (Spinner) findViewById(R.id.spnDistrict);

        spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject jDistricts = result.getJSONObject(i);
                    System.out.println(jDistricts);
                    JSONArray districts = jDistricts.getJSONArray("districts");
                    arrayDistricts.clear();
                    for (int j=0; j < districts.length(); j++){

                        arrayDistricts.add(districts.getString(j));
                    }
                    spnDistrict.setAdapter(new ArrayAdapter<String>(preSignUpPage.this, android.R.layout.simple_spinner_dropdown_item, arrayDistricts));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        try {

            JSONObject objStates = new JSONObject(OpenJSON.readJSONFromAsset(this, "states-and-districts.json"));
            result = objStates.getJSONArray("states");
            getStates(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getStates(JSONArray j){
        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                states.add(json.getString("state"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Setting adapter to show the items in the spnState
        spnState.setAdapter(new ArrayAdapter<String>(preSignUpPage.this, android.R.layout.simple_spinner_dropdown_item, states));
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnNext:
                if(isFormValid()){
                    sendToSignUp();
                }
                break;
        }
    }

    private boolean isFormValid() {
        if(txtEFirstName.getText().toString().trim().isEmpty()){
            txtEFirstName.setError("Required Field");
            txtEFirstName.requestFocus();
            return false;
        }
        else if(txtELastName.getText().toString().trim().isEmpty()){
            txtELastName.setError("Required Field");
            txtELastName.requestFocus();
            return false;
        }
        else if(txtEUserName.getText().toString().trim().isEmpty()){
            txtEUserName.setError("Required Field");
            txtEUserName.requestFocus();
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(txtEEmail.getText().toString().trim()).matches()){
            txtEEmail.setError("Enter a valid email Address");
            txtEEmail.requestFocus();
            return false;
        }
        else if(txtEPassword.getText().toString().length() < 6){
            txtEPassword.setError("Password should be minimum 6 characters long");
            txtEPassword.requestFocus();
            return false;
        }
        else if(!(txtEPassword.getText().toString().equals(txtEConfirmPassword.getText().toString()))){
            txtEConfirmPassword.setError("Password does not match", null);
            txtEConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void sendToSignUp() {
        if(!checkIfUserNameExits(txtEUserName.getText().toString().trim())){
//            Intent intent = new Intent(preSignUpPage.this, NextSignUpPage.class);
//            intent.putExtra("FirstName", txtEFirstName.getText().toString().trim());
//            intent.putExtra("LastName", txtELastName.getText().toString().trim());
//            intent.putExtra("UserName", txtEUserName.getText().toString().trim());
//            intent.putExtra("Email", txtEEmail.getText().toString().trim());
//            intent.putExtra("Password", txtEPassword.getText().toString().trim());
//            startActivity(intent);
        }
        else{
            Toast.makeText(this, "Username already taken.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkIfUserNameExits(String UserName) {
        //code to check if username exists...
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch(view.getId()){
            case R.id.txtELastName:
                if(b && txtEFirstName.getText().toString().trim().isEmpty()){
                    txtEFirstName.setError("Required Field");
                    txtEFirstName.requestFocus();
                }
                break;
            case R.id.txtEUserName:
                if(b && txtEFirstName.getText().toString().trim().isEmpty()){
                    txtEFirstName.setError("Required Field");
                    txtEFirstName.requestFocus();
                }
                else if(b && txtELastName.getText().toString().trim().isEmpty()){
                    txtELastName.setError("Required Field");
                    txtELastName.requestFocus();
                }
                break;
            case R.id.txtEEmail:
                if(b && txtEFirstName.getText().toString().trim().isEmpty()){
                    txtEFirstName.setError("Required Field");
                    txtEFirstName.requestFocus();
                }
                else if(b && txtELastName.getText().toString().trim().isEmpty()){
                    txtELastName.setError("Required Field");
                    txtELastName.requestFocus();
                }
                else if(b && txtEUserName.getText().toString().trim().isEmpty()){
                    txtEUserName.setError("Required Field");
                    txtEUserName.requestFocus();
                }
                break;
            case R.id.txtEPassword:
                if(b && txtEFirstName.getText().toString().trim().isEmpty()){
                    txtEFirstName.setError("Required Field");
                    txtEFirstName.requestFocus();
                }
                else if(b && txtELastName.getText().toString().trim().isEmpty()){
                    txtELastName.setError("Required Field");
                    txtELastName.requestFocus();
                }
                else if(b && txtEUserName.getText().toString().trim().isEmpty()){
                    txtEUserName.setError("Required Field");
                    txtEUserName.requestFocus();
                }
                else if(b && !Patterns.EMAIL_ADDRESS.matcher(txtEEmail.getText().toString().trim()).matches()){
                    txtEEmail.setError("Enter a valid email Address");
                    txtEEmail.requestFocus();
                }
                break;
            case R.id.txtEConfirmPassword:
                if(b && txtEFirstName.getText().toString().trim().isEmpty()){
                    txtEFirstName.setError("Required Field");
                    txtEFirstName.requestFocus();
                }
                else if(b && txtELastName.getText().toString().trim().isEmpty()){
                    txtELastName.setError("Required Field");
                    txtELastName.requestFocus();
                }
                else if(b && txtEUserName.getText().toString().trim().isEmpty()){
                    txtEUserName.setError("Required Field");
                    txtEUserName.requestFocus();
                }
                else if(b && !Patterns.EMAIL_ADDRESS.matcher(txtEEmail.getText().toString().trim()).matches()){
                    txtEEmail.setError("Enter a valid email Address");
                    txtEEmail.requestFocus();
                }
                else if(b && txtEPassword.getText().toString().length() < 6){
                    txtEPassword.setError("Password should be minimum 6 characters long");
                    txtEPassword.requestFocus();
                }
                break;
        }
    }
}