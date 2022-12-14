package com.example.authapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authapp.R;
import com.example.authapp.classes.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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
        holder.username.setText(list.get(position).getUsername());
         holder.name.setText(list.get(position).getName());
        holder.branch.setText(list.get(position).getBranch());
        String ImageUrl = list.get(position).getProfileThumbUrl();
        Log.v("ImageUrl",ImageUrl);
        holder.loadToImage(ImageUrl);

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
        CircleImageView Search_user_image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name=itemView.findViewById(R.id.txtVName);
            Search_user_image = itemView.findViewById(R.id.Search_user_image);
            username=itemView.findViewById(R.id.txtVUserName);
            branch=itemView.findViewById(R.id.txtVBranch);
        }
        public void loadToImage(String imageUrl) {
//            RequestOptions placeholderRequest = new RequestOptions();
//            placeholderRequest.placeholder(R.drawable.profile_placeholder);
            Picasso.get().load(imageUrl).into(Search_user_image);
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
