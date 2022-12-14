package com.example.authapp;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

public class FullScreen extends AppCompatActivity {

    private ImageView fullImage;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        fullImage = findViewById(R.id.fullImage);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        Bundle extras = getIntent().getExtras();
        String imgUrl = extras.getString("URL");

        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.profile_placeholder);

        try {
//            Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(imgUrl).into(fullImage);
            Picasso.get().load(imgUrl).into(fullImage);
//

        }catch (Exception e){
//                Toast.makeText(context, "user image not present", Toast.LENGTH_SHORT).show();
        }
    }
}