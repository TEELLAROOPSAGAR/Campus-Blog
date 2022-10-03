package com.example.authapp.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authapp.Others_Profile_Page;
import com.example.authapp.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    private  Context context;
     private ArrayList<User> list;
     private RecyclerViewClickListener listener;

    public MyAdapter( ArrayList<User> list,RecyclerViewClickListener listener){
        //this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
         User user = list.get(position);
        holder.username.setText("username  : "+user.getUsername());
         holder.name.setText("Name   : "+user.getName());
        holder.branch.setText("branch  : "+user.getBranch());
        holder.email.setText("email    : "+user.getEmail());

    }

    @Override
    public int getItemCount(){
        return list.size();
    }
    public interface RecyclerViewClickListener {
        void onClick(View v,int position);
    }
    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name,email,username,branch;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name=itemView.findViewById(R.id.txtVName);
            email=itemView.findViewById(R.id.txtVEmailAddress);
            username=itemView.findViewById(R.id.txtVUserName);
            branch=itemView.findViewById(R.id.txtVBranch);
        }

        @Override
        public void onClick(View view) {
           listener.onClick(view, getAdapterPosition());
        }

//        @Override
//        public void onClick(View v){
//           final Intent intent ;
//           intent = new Intent(context, Others_Profile_Page.class);
//           context.startActivity(intent);
//        }
   }


}
