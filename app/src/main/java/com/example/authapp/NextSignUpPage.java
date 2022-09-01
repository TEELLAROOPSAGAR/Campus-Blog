package com.example.authapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.authapp.jsonFiles.OpenJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NextSignUpPage extends AppCompatActivity {

    private Spinner spnColleges;
    private Spinner spnBranch;
    private Spinner spnYear;
    private ArrayList<String> clgList;
    private ArrayList<String> yearList;
    private ArrayList<String> branchList;


    private JSONArray result;
    JSONObject j = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_sign_up_page);

        spnColleges = (Spinner)findViewById(R.id.spnColleges);
        spnYear = (Spinner)findViewById(R.id.spnYear);
        spnBranch=(Spinner)findViewById(R.id.spnBranch);

        clgList = new ArrayList<String>();
        yearList = new ArrayList<String>();
        branchList = new ArrayList<String>();

        //year spinner
        yearList.add("year");
        int t=2000;
        while(t<=2022){
            yearList.add(Integer.toString(t));
            t++;
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnYear.setAdapter(yearAdapter);
        //end of year spinner
        //branch list start this is for sample
        branchList.add("Select Branch");
        branchList.add("CSE");
        branchList.add("MECH");
        branchList.add("ECE");
        branchList.add("CIVIL");
        ArrayAdapter<String> branchAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, branchList);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBranch.setAdapter(branchAdapter);
        //branch list end
        spnColleges.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject jDistricts = null;
                try {
                    jDistricts = result.getJSONObject(0);
                    System.out.println(jDistricts);
                    JSONArray districts = jDistricts.getJSONArray("colleges");

                    for (int j=0; j < districts.length(); j++){

                        clgList.add(districts.getString(j));
                    }
                    spnColleges.setAdapter(new ArrayAdapter<String>(NextSignUpPage.this, android.R.layout.simple_spinner_dropdown_item, clgList));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        try {
            JSONObject objStates = new JSONObject(OpenJSON.readJSONFromAsset(this, "colleges_List.json"));

            result = objStates.getJSONArray("colleges");
            getCollege(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //
    private void getCollege(JSONArray j){
        //Traversing through all the items in the json array
        for(int i=0;i<j.length();i++){
            try {
                clgList.add(j.getString(i));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Setting adapter to show the items in the spinner
        spnColleges.setAdapter(new ArrayAdapter<String>(NextSignUpPage.this, android.R.layout.simple_spinner_dropdown_item, clgList));
    }
}