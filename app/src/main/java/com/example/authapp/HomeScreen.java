package com.example.authapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    private FloatingActionButton AddPostBtn;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    private BlogAdapter blogAdapter;
    private ArrayList<postUploadInfo> blog_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        recyclerView = findViewById(R.id.All_posts);
        AddPostBtn = findViewById(R.id.add_img);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .build();
//        firebaseFirestore.setFirestoreSettings(settings);

        blog_list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        blogAdapter = new BlogAdapter(blog_list,this);
        recyclerView.setAdapter(blogAdapter);

        firebaseFirestore.collection("Blogs").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                 if(!queryDocumentSnapshots.isEmpty()){
                     List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                     for(DocumentSnapshot d : list){
                         postUploadInfo p = d.toObject(postUploadInfo.class);
                         blog_list.add(p);

                     }
                     blogAdapter.notifyDataSetChanged();
                 }else{
                     Toast.makeText(HomeScreen.this, "Data not found", Toast.LENGTH_SHORT).show();
                 }
            }
        });

//        blog_list = new ArrayList<>();
//        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list, HomeScreen.this);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        AddPostBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HomeScreen.this,Add_blog.class));
//            }
//        });
//        Query query = FirebaseFirestore.getInstance()
//                .collection("Blogs")
////                .orderBy("timestamp")
//                .limit(50);
//        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//
//            }
//        })

//        if(mAuth.getCurrentUser() != null) {
//            firebaseFirestore.collection("Blogs")
//                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                            try {
//                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
//                                    if (doc.getType() == DocumentChange.Type.ADDED) {
//                                        String blogPostId  = doc.getDocument().getId();
//                                        postUploadInfo blogPost = doc.getDocument().toObject(postUploadInfo.class);
//                                        blog_list.add(blogPost);
//
//                                /*if(isFirstPageFirstLoad) {
//                                    blog_list.add(blogPost);
//                                }else {
//                                    blog_list.add(0,blogPost);
//
//                                }*/
//                                        blogRecyclerAdapter.notifyDataSetChanged();
//                                        //   blog_list_view.scrollToPosition(0);
//
//                                    }
//                                }
//                                //isFirstPageFirstLoad=false;
//
//                            } catch (Exception ex) {
//
//                            }
//                        }
//                    });
//        }
//
//
//
//
//        recyclerView.setAdapter(blogRecyclerAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

}