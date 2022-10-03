package com.example.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authapp.utils.MyAdapter;
import com.example.authapp.utils.User;
import com.google.firebase.auth.FirebaseAuth;
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
    private Button btnLogout ;
    private SearchView searchView;
    private TextView txtVNoDataFound;
    private ImageView ProfileImage;
    private MyAdapter.RecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        txtVNoDataFound= findViewById(R.id.txtVNoDataFound);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SearchPage.this,MainActivity.class));
                Toast.makeText(SearchPage.this, "Logged out", Toast.LENGTH_SHORT).show();
            }
        });


        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterStudent(newText);
                return true;
            }
        });

        ProfileImage = findViewById(R.id.ProfileImage);
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(SearchPage.this,MyProfile.class));
            }
        });

    }

    private void filterStudent(String text) {
        setOnClickListener();
        recyclerview = findViewById(R.id.userList);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        myadapter = new MyAdapter(list,listener);
        recyclerview.setAdapter(myadapter);

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot datasnapshot : dataSnapshot.getChildren()){
                    User user = datasnapshot.getValue(User.class);
                    assert user != null;
                    if(user.getName().replaceAll("\\s+","").toLowerCase().contains((text.replaceAll("\\s+","")).toLowerCase()))
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
                Intent intent= new Intent(getApplicationContext(),Others_Profile_Page.class);
                intent.putExtra("username",list.get(position).getUsername());
                intent.putExtra("name",list.get(position).getName());
//                intent.putExtra("college",list.get(position).getCollege());
                intent.putExtra("branch",list.get(position).getBranch());
//                intent.putExtra("age",list.get(position).getAge());
//                intent.putExtra("phone",list.get(position).getPhone());
                intent.putExtra("email",list.get(position).getEmail());
                startActivity(intent);
            }
        };
    }
}