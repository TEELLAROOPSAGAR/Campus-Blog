package com.example.authapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.authapp.MyProfile;
import com.example.authapp.R;
import com.example.authapp.classes.Comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;

    public CommentsRecyclerAdapter(List<Comments> commentsList){

        this.commentsList = commentsList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        firebaseFirestore=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String commentMessage = commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);

        try {

            Date date = new Date();
            Date cmtTime = commentsList.get(position).getTimestamp();
            SimpleDateFormat myFormatObj = new SimpleDateFormat("E, MMM dd yyyy HH:mm");

            String formattedDate = myFormatObj.format(cmtTime);
            holder.setCommentTime(formattedDate);

        } catch (Exception ex) {


            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            holder.setCommentTime(currentDateTimeString);

        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        String user_id=commentsList.get(position).getUser_id();
        databaseReference.child(user_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
//                    UserNameInPost = task.getResult().child("username").getValue().toString();
                    holder.setUserImageAndName(task.getResult().child("name").getValue().toString(),task.getResult().child("profileThumbUrl").getValue().toString());
                }
            }
            });

        holder.commentUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(context, MyProfile.class);
                profileIntent.putExtra("User_id",user_id);
                context.startActivity(profileIntent);
            }
        });
//        firebaseFirestore.collection("Blogs").document(commentsList.get(position).BlogPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                if (task.isSuccessful()){
//                    holder.setUserImageAndName(task.getResult().getString("userName"),
//                            task.getResult().getString("imageUrls"),
//                            task.getResult().getString("thumbUrl"));
//                }
//            }
//        });


    }


    @Override
    public int getItemCount() {

        if(commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView commentMessage;
        private TextView commentTime;
        private TextView commentUserName;
        private CircleImageView commentUserImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            commentUserName = mView.findViewById(R.id.comment_username);
        }

        public void setComment_message(String message){

            commentMessage = mView.findViewById(R.id.comment_message);
            commentMessage.setText(message);

        }

        public void setCommentTime(String cmtTime) {

        commentTime=mView.findViewById(R.id.comment_time);
        commentTime.setText(cmtTime);
        }

        public void setUserImageAndName(String name, String thumb_url) {

//            commentUserName=mView.findViewById(R.id.comment_username);
            commentUserImage=mView.findViewById(R.id.comment_image);

            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.profile_placeholder);

            commentUserName.setText(name);
//            Glide.with(context).applyDefaultRequestOptions(placeholderRequest)
//                    .load(image).thumbnail(Glide.with(context).load(thumb_url)).into(commentUserImage);
//            Glide.with(context).load(thumb_url).into(commentUserImage);
            Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(thumb_url).into(commentUserImage);


        }
    }

}
