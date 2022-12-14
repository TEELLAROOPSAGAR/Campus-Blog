package com.example.authapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class CropperActivity extends AppCompatActivity {

    String result;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);

        readIntent();

        UCrop.Options options = new UCrop.Options();

        String dest_Uri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
        UCrop.of(fileUri,Uri.fromFile(new File(getCacheDir(),dest_Uri)))
                .withOptions(options)
                .withAspectRatio(0,0)
                .useSourceImageAspectRatio()
                .withMaxResultSize(2000,2000)
                .start(CropperActivity.this);
    }

    private void readIntent() {
        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            result = intent.getStringExtra("DATA");
            fileUri = Uri.parse(result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            final Uri resultUri = UCrop.getOutput(data);
            Log.v("filepathuri",resultUri.toString());
            Intent returnIntent = new Intent();
            returnIntent.putExtra("ResultUri",resultUri+"");
            setResult(-1,returnIntent);
            finish();
        }else{
            Toast.makeText(CropperActivity.this ,"crop error occurred", Toast.LENGTH_SHORT).show();
        }
    }
}