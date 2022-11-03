package com.example.authapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.authapp.Adapters.BlogRecyclerAdapter;
import com.example.authapp.classes.postUploadInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {

    private FloatingActionButton AddPostBtn;
    private Toolbar mainToolbar;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    SwipeRefreshLayout swipeRefreshLayout;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private ArrayList<postUploadInfo> blog_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        recyclerView = findViewById(R.id.All_posts);
        AddPostBtn = findViewById(R.id.add_img);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadPosts();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
//        mainToolbar = findViewById(R.id.main_toolBar);
//        setSupportActionBar(mainToolbar);
//        getSupportActionBar().setTitle("Blogs");

        AddPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeScreen.this,Add_blog.class));
            }
        });

       LoadPosts();

    }

    private void LoadPosts() {
        blog_list = new ArrayList<>();
        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list, HomeScreen.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


//        Query query = FirebaseFirestore.getInstance()
//                .collection("Blogs")
////                .orderBy("timestamp")
//                .limit(50);
//        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//
//            }
//        });


        if(mAuth.getCurrentUser() != null) {
            firebaseFirestore.collection("Blogs").orderBy("timeStamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            try {
                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        String blogPostId  = doc.getDocument().getId();

                                        Log.v("blogIDH",blogPostId);
                                        postUploadInfo blogPost = doc.getDocument().toObject(postUploadInfo.class).withId(blogPostId);
                                        blog_list.add(blogPost);

                                /*if(isFirstPageFirstLoad) {
                                    blog_list.add(blogPost);
                                }else {
                                    blog_list.add(0,blogPost);

                                }*/
                                        blogRecyclerAdapter.notifyDataSetChanged();
                                        //   blog_list_view.scrollToPosition(0);

                                    }
                                }
                                //isFirstPageFirstLoad=false;

                            } catch (Exception ex) {

                            }
                        }
                    });
        }
        recyclerView.setAdapter(blogRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
                Intent i=new Intent(HomeScreen.this,MainActivity.class);
                startActivity(i);
            finish();

        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_view_profile:

                Intent intent = new Intent(HomeScreen.this, MyProfile.class);
                intent.putExtra("User_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
                return true;

            default:
                return false;


        }

    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(HomeScreen.this,MainActivity.class);
        startActivity(loginIntent);
        finish();

    }
}