package com.example.authapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authapp.Adapters.MyAdapter;
import com.example.authapp.classes.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchPage extends AppCompatActivity {
    private DatabaseReference reference;
    private MyAdapter myadapter;
    private ArrayList<User> list;
    private RecyclerView recyclerview;
    private SearchView searchView;
    private TextView txtVNoDataFound;
    private MyAdapter.RecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerview = findViewById(R.id.userList);
        txtVNoDataFound= findViewById(R.id.txtVNoDataFound);
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()<3){
                    list = new ArrayList<>();
                    myadapter = new MyAdapter(list,listener);
                    recyclerview.setAdapter(myadapter);
                    return false;
                }
                if(!newText.isEmpty())
                    filterStudent(newText);
                   return true;
            }
        });

    }

    private void filterStudent(String text) {
        setOnClickListener();
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        myadapter = new MyAdapter(list,listener);
        recyclerview.setAdapter(myadapter);
//                    this sends a email to user to verify

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
//                String userId  = dataSnapshot.getKey();
//                Log.v("userId",userId);
                for(DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    String userId = datasnapshot.getKey();
                    Log.v("userId", userId);

                    User user = datasnapshot.getValue(User.class).withId(userId);
                    assert user != null;
                        if (user.getName().replaceAll("\\s+", "").toLowerCase().contains((text.replaceAll("\\s+", "")).toLowerCase()))
                            list.add(user);
                    }



                if(list.isEmpty())  txtVNoDataFound.setText("oops!No data found");
                else{
                    txtVNoDataFound.setText("");
                }
                myadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchPage.this,"Something Wrong happened!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setOnClickListener() {
        listener = new MyAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent= new Intent(getApplicationContext(),MyProfile.class);
                intent.putExtra("User_id",list.get(position).GetUserId);
                startActivity(intent);
            }
        };
    }
}