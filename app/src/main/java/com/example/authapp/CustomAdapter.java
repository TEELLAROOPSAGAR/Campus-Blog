package com.example.authapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import java.util.ArrayList;

public class CustomAdapter implements ListAdapter {
    ArrayList<Uri>ImageList;
    Context context;
    public CustomAdapter(Context context, ArrayList<Uri> ImageList){
        this.ImageList = ImageList;
        this.context = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        Log.v("listsize", ImageList.size() + "");
        return ImageList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Uri uri = ImageList.get(i);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_row, null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            ImageView imag = convertView.findViewById(R.id.img);
//            Log.v("seturi", uri.toString());
            imag.setImageURI(uri);
//            Picasso.with(context)
//                    .load(uri)
//                    .into(imag);
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return ImageList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
