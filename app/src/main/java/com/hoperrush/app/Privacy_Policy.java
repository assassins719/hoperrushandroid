package com.hoperrush.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperrush.R;

public class Privacy_Policy extends AppCompatActivity {
    private RelativeLayout back;
    private TextView header_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy__policy);


        back = (RelativeLayout) findViewById(R.id.aboutus_header_back_layout);
        header_txt = (TextView)findViewById(R.id.aboutus_header_textview);
        header_txt.setText("Privacy & Policy");
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
