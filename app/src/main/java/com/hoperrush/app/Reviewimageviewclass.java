package com.hoperrush.app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hoperrush.R;
import com.hoperrush.hockeyapp.ActivityHockeyApp;

public class Reviewimageviewclass extends ActivityHockeyApp {
    ImageView reviewimage;
    String image="";
    RelativeLayout bacarrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskerprofileimage);
        intialize();

//        bacarrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//                finish();
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//            }
//        });
    }

    public void intialize(){
//        bacarrow=(RelativeLayout)findViewById(R.id.layout_back_image);
//
//        reviewimage=(ImageView)findViewById(R.id.reviewimage);
//        Intent i=getIntent();
//        image=i.getExtras().getString("reviewimage");
//
//        Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.nouserimg).memoryPolicy(MemoryPolicy.NO_CACHE).into(reviewimage);

    }
}
