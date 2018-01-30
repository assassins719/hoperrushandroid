package com.hoperrush.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hoperrush.R;
import com.hoperrush.hockeyapp.ActivityHockeyApp;

/**
 * Created by user145 on 8/4/2017.
 */

public class Terms_Conditions extends ActivityHockeyApp {
    private RelativeLayout back;
    private TextView header_txt;
    private WebView aboutus_webview;
    String web_url = "",header = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_conditions);

        Intent data = getIntent();
        web_url = data.getStringExtra("url");
        header = data.getStringExtra("header");

        aboutus_webview=(WebView)findViewById(R.id.aboutus_webview);
        back = (RelativeLayout) findViewById(R.id.aboutus_header_back_layout);
        header_txt = (TextView)findViewById(R.id.aboutus_header_textview);
        header_txt.setText(header);
        aboutus_webview.loadUrl(web_url);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            onBackPressed();
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);
            return true;
        }
        return false;
    }
}
