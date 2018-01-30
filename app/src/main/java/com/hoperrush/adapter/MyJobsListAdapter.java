package com.hoperrush.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.app.ChatPage;
import com.hoperrush.app.MyJobs;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.socket.ChatMessageService;
import com.hoperrush.core.widgets.RoundedImageView;
import com.hoperrush.pojo.MyJobsListPojo;
import com.hoperrush.utils.RoundedCurveTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Casperon Technology on 1/12/2016.
 */
public class MyJobsListAdapter extends BaseAdapter {

    private ArrayList<MyJobsListPojo> data;
    private LayoutInflater mInflater;
    private Activity context;
    private int lastPosition = -1;
    final int PERMISSION_REQUEST_CODE = 111;

    final int PERMISSION_REQUEST_CODES = 222;

    final int PERMISSION_REQUEST_CODES2 = 333;

    public MyJobsListAdapter(Activity c, ArrayList<MyJobsListPojo> d) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }


    public class ViewHolder {
        private TextView Tv_scheduled, Tv_status, Tv_serviceType, Tv_OrderId, Tv_scheduled_time;
        private RoundedImageView Im_icon;
        private LinearLayout Ll_Chat, Ll_Call, Ll_cancel;
        private RelativeLayout Rl_option;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.myjobs_single, parent, false);
            holder = new ViewHolder();
            holder.Tv_scheduled = (TextView) view.findViewById(R.id.myJobs_single_scheduled_date_textView);
            holder.Tv_status = (TextView) view.findViewById(R.id.myJobs_single_status_textView);
            holder.Tv_serviceType = (TextView) view.findViewById(R.id.myJobs_single_service_type_textView);
            holder.Tv_OrderId = (TextView) view.findViewById(R.id.myJobs_single_orderId_textView);
            holder.Im_icon = (RoundedImageView) view.findViewById(R.id.myJobs_single_imageView);
            holder.Tv_scheduled_time = (TextView) view.findViewById(R.id.myJobs_single_scheduled_time_textView);

            holder.Ll_Chat = (LinearLayout) view.findViewById(R.id.myJobs_single_chat_layout);
            holder.Ll_Call = (LinearLayout) view.findViewById(R.id.myJobs_single_call_layout);
            holder.Ll_cancel = (LinearLayout) view.findViewById(R.id.myJobs_single_cancel_layout);
            holder.Rl_option = (RelativeLayout) view.findViewById(R.id.myJobs_single_option_layout);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

       /* Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        view.startAnimation(animation);
        lastPosition = position;*/

        holder.Tv_scheduled.setText(data.get(position).getJob_date());
        holder.Tv_serviceType.setText(data.get(position).getService_type());
        holder.Tv_OrderId.setText(data.get(position).getJob_id());
        holder.Tv_status.setText(data.get(position).getJob_status());
        holder.Tv_scheduled_time.setText(data.get(position).getJob_time());


        if (data.get(position).getJob_status().equalsIgnoreCase("Booked")) {
            holder.Tv_status.setTextColor(Color.parseColor("#FF5E00"));
        } else if (data.get(position).getJob_status().equalsIgnoreCase("Accepted")) {
            holder.Tv_status.setTextColor(Color.parseColor("#028900"));
        } else if (data.get(position).getJob_status().equalsIgnoreCase("Closed")) {
            holder.Tv_status.setTextColor(Color.parseColor("#FF2D55"));
        } else if (data.get(position).getJob_status().equalsIgnoreCase("Confirmed")) {
            holder.Tv_status.setTextColor(Color.parseColor("#1A858F"));
        } else if (data.get(position).getJob_status().equalsIgnoreCase("Completed")) {
            holder.Tv_status.setTextColor(Color.parseColor("#006411"));
        } else if (data.get(position).getJob_status().equalsIgnoreCase("Cancelled")) {
            holder.Tv_status.setTextColor(Color.parseColor("#CC0000"));
        } else if (data.get(position).getJob_status().equalsIgnoreCase("Expired")) {
            holder.Tv_status.setTextColor(Color.parseColor("#E12080"));
        }


        //Call Layout Show/Hide Function
        if (data.get(position).getDoCall().equalsIgnoreCase("Yes")) {
            holder.Ll_Call.setVisibility(View.VISIBLE);
        } else {
            holder.Ll_Call.setVisibility(View.GONE);
        }

        //Chat Layout Show/Hide Function
        if (data.get(position).getDoMsg().equalsIgnoreCase("Yes")) {
            holder.Ll_Chat.setVisibility(View.VISIBLE);
        } else {
            holder.Ll_Chat.setVisibility(View.GONE);
        }

        //Cancel Layout Show/Hide Function
        if (data.get(position).getDoCancel().equalsIgnoreCase("Yes") && !data.get(position).getJob_status().equalsIgnoreCase("StartJob")) {
            holder.Ll_cancel.setVisibility(View.VISIBLE);
        } else {
            holder.Ll_cancel.setVisibility(View.GONE);
        }

        //Show and Hide the Option Layout
        if (data.get(position).getDoCall().equalsIgnoreCase("No") && data.get(position).getDoMsg().equalsIgnoreCase("No") && data.get(position).getDoCancel().equalsIgnoreCase("No")) {
            holder.Rl_option.setVisibility(View.GONE);
        } else {
            holder.Rl_option.setVisibility(View.VISIBLE);
        }

        Picasso.with(context).load(data.get(position).getService_icon()).transform(new RoundedCurveTransformation(10, 0)).fit().into(holder.Im_icon);

        holder.Ll_Call.setOnClickListener(new onCallClickListener(position));
        holder.Ll_cancel.setOnClickListener(new onCancelClickListener(position));
        holder.Ll_Chat.setOnClickListener(new onChatClickListener(position));

        return view;
    }

    private class onCallClickListener implements View.OnClickListener {
        int mPosition;

        private onCallClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {



            String support=data.get(mPosition).getIsSupport();
            String countrycode= data.get(mPosition).get_countrycode();
            String contactnumber=data.get(mPosition).getContact_number();

            MyJobs MyJobsPage = (MyJobs) context;
            MyJobsListPojo  tat = data.get(mPosition);
            MyJobsPage.callposition(support,countrycode,contactnumber);


        }
    }


    //--------------Alert Method-----------
    private void Alert(String title, String alert) {
        final PkDialog mDialog = new PkDialog(context);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(context.getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }


    private class onCancelClickListener implements View.OnClickListener {
        int mPosition;

        private onCancelClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            MyJobs MyJobsPage = (MyJobs) context;
            MyJobsListPojo tat = data.get(mPosition);
            MyJobsPage.cancelJobReason(data.get(mPosition).getJob_id(), mPosition);
        }
    }

    private class onChatClickListener implements View.OnClickListener {
        int mPosition;

        private onChatClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            ChatMessageService.tasker_id = "";
            ChatMessageService.task_id = "";
            Intent intent = new Intent(context, ChatPage.class);
            intent.putExtra("JobID-Intent", data.get(mPosition).getJob_id());
            intent.putExtra("TaskerId", data.get(mPosition).getTaskerid());
            intent.putExtra("TaskId", data.get(mPosition).getTaskid());


            System.out.println("TaskId----------" + data.get(mPosition).getTaskid());

            System.out.println("TaskerId1----------" + data.get(mPosition).getTaskerid());

            context.startActivity(intent);
            context.overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }


}
