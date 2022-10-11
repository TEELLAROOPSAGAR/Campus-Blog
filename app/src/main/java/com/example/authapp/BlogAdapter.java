package com.example.authapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {

    ArrayList<postUploadInfo> blog_list;
    Context context;

    public BlogAdapter(ArrayList<postUploadInfo> blog_list, Context context){

        this.blog_list = blog_list;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.blog_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          postUploadInfo info = blog_list.get(position);
          holder.blog_title.setText(info.getTitle());
//        Glide.with(context).load(info.getImageURLs().get(0)).into(holder.blogImageView);
        Glide.with(context).load(info.getThumbUrl()).into(holder.blogUserimg);
        Toast.makeText(context, info.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView descView;
        private ImageView blogImageView;
        private TextView blogDate;
        private TextView  blogUsername;
        private ImageView blogUserimg;

        private ImageView blogLikeBtn;
        private TextView blogLikeCount;
        private ImageView postDeleteBtn;
        private ImageView blogCommentBtn;
        private TextView blogCommentCount;
        private TextView blog_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            descView = itemView.findViewById(R.id.blog_desc);
            blog_title = itemView.findViewById(R.id.blog_title);
            blogImageView = itemView.findViewById(R.id.blog_image);
            blogUserimg = itemView.findViewById(R.id.blog_user_image);
            blogDate =itemView.findViewById(R.id.blog_date);
            blogUsername =itemView.findViewById(R.id.blog_user_name);
        }
    }
}
