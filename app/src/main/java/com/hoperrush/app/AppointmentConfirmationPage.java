package com.hoperrush.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.utils.SubClassActivity;

/**
 * Casperon Technology on 1/9/2016.
 */
public class AppointmentConfirmationPage extends SubClassActivity {

    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;
    private TextView Tv_title, Tv_orderId, Tv_date, Tv_serviceType, Tv_description;
    private Button Bt_done;
    private String sMessage = "", sOrderId = "", sDate = "", sServiceType = "", sDescription = "";
    private String getMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_confirmation_page);
        initializeHeaderBar();
        initialize();

        Bt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
               /*Intent broadcastIntent_appoimentconmfirmation = new Intent();
                broadcastIntent_appoimentconmfirmation.setAction("com.finish.AppoimentConfirmation");
                sendBroadcast(broadcastIntent_appoimentconmfirmation);*/

                Intent broadcastIntent_appoiment = new Intent();
                broadcastIntent_appoiment.setAction("com.finish.AppoimentPage");
                sendBroadcast(broadcastIntent_appoiment);

                Intent broadcastIntent_subcategories = new Intent();
                broadcastIntent_subcategories.setAction("com.finish.CategoriesDetailsPage");
                sendBroadcast(broadcastIntent_subcategories);

                Intent broadcastIntent_map_page = new Intent();
                broadcastIntent_map_page.setAction("com.refresh.map_page");
                sendBroadcast(broadcastIntent_map_page);

            }
        });
    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_title_textView);

        Rl_back.setVisibility(View.GONE);

        Tv_headerTitle.setText(getResources().getString(R.string.appointment_confirmation_label_title));
        Im_backIcon.setImageResource(R.drawable.back_arrow);
    }

    private void initialize() {
        Tv_title = (TextView) findViewById(R.id.appointment_confirmation_title_textView);
        Tv_orderId = (TextView) findViewById(R.id.appointment_confirmation_booking_id_textView);
        Tv_date = (TextView) findViewById(R.id.appointment_confirmation_booking_date_textView);
        Tv_serviceType = (TextView) findViewById(R.id.appointment_confirmation_booking_service_textView);
        Tv_description = (TextView) findViewById(R.id.appointment_confirmation_booking_description_textView);
        Bt_done = (Button) findViewById(R.id.appointment_confirmation_page_done_button);
        Intent intent = getIntent();
        sMessage = intent.getStringExtra("IntentMessage");
        sOrderId = intent.getStringExtra("IntentJobID");
        sDate = intent.getStringExtra("IntentOrderDate");
        sServiceType = intent.getStringExtra("IntentServiceType");
        sDescription = intent.getStringExtra("IntentDescription");

        Tv_title.setText(sMessage);
        Tv_orderId.setText(sOrderId);
        Tv_date.setText(sDate);
        Tv_serviceType.setText(sServiceType);
        Tv_description.setText(sDescription);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            Intent broadcastIntent_map_page = new Intent();
            broadcastIntent_map_page.setAction("com.refresh.map_page");
            sendBroadcast(broadcastIntent_map_page);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }


}
