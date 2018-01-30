package com.hoperrush.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.hoperrush.R;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class Profilpictureview extends ActivityHockeyApp {
ImageView image;
    String userimage="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilpictureview);
        image=(ImageView)findViewById(R.id.image);
        Intent i=getIntent();
        userimage=i.getExtras().getString("image");
        Picasso.with(getApplicationContext()).load(userimage).error(R.drawable.placeholder_icon)
                .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(image);




    }
}
