package com.hoperrush.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.pojo.ChatPojo;

import java.util.ArrayList;

/**
 * Casperon Technology on 1/29/2016.
 */
public class ChatAdapter extends BaseAdapter {

    private ArrayList<ChatPojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public ChatAdapter(Context c, ArrayList<ChatPojo> d) {
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
        public ImageView messageStatus, avatar;
        private TextView Tv_rightMessage, myRightTXTTime;
        private TextView Tv_leftMessage, myLeftTXTTime;
        private RelativeLayout Rl_left, Rl_right;
        RelativeLayout warning;
        public ImageView readmessage_status;
        private ImageView myRightSeenStatusIMG;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.chat_single, parent, false);
            holder = new ViewHolder();
            holder.Tv_rightMessage = (TextView) view.findViewById(R.id.chat_right_message_text);
            holder.avatar = (ImageView) view.findViewById(R.id.imageview_left);
            holder.Tv_leftMessage = (TextView) view.findViewById(R.id.chat_left_message_text);
            holder.Rl_left = (RelativeLayout) view.findViewById(R.id.chat_left_main_layout);
            holder.Rl_right = (RelativeLayout) view.findViewById(R.id.chat_right_main_layout);
            holder.warning=(RelativeLayout)view.findViewById(R.id.warning);
            holder.myRightTXTTime = (TextView) view.findViewById(R.id.chat_right_TXT_time);
            holder.myRightSeenStatusIMG = (ImageView) view.findViewById(R.id.user_reply_status);
            holder.myLeftTXTTime = (TextView) view.findViewById(R.id.chat_left_TXT_time);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        String url = data.get(position).getURL();
        //Picasso.with(context).load(Iconstant.SOCKET_HOST_URL + url).into(holder.avatar);
        String type = data.get(position).getType();
        String message = data.get(position).getMessage();
        if (type.equalsIgnoreCase("SELF")) {
            if(data.get(position).getwarnings().equalsIgnoreCase("Yes")&& position==0){

                holder.warning.setVisibility(View.VISIBLE);
            }else{

                holder.warning.setVisibility(View.GONE);
            }


            holder.Rl_left.setVisibility(View.GONE);
            holder.myLeftTXTTime.setVisibility(View.GONE);
            holder.Rl_right.setVisibility(View.VISIBLE);
            holder.Tv_rightMessage.setText(data.get(position).getMessage().trim());
            holder.myRightSeenStatusIMG.setVisibility(View.VISIBLE);
            if (!data.get(position).getSeenStatus().equals("")) {
                if (data.get(position).getSeenStatus().equals("1")) {
                    holder.myRightSeenStatusIMG.setImageResource(R.drawable.icon_double_tick_unseen);
                } else {
                    holder.myRightSeenStatusIMG.setImageResource(R.drawable.icon_double_tick_seen);
                }
            }
            if (!data.get(position).getDate().equals("")) {
                holder.myRightTXTTime.setVisibility(View.VISIBLE);
                String[] aSplitDateStr = data.get(position).getDate().split(", ");
                holder.myRightTXTTime.setText(aSplitDateStr[1]);
            } else {
                holder.myRightTXTTime.setVisibility(View.GONE);
            }
        } else if (type.equalsIgnoreCase("OTHER")) {

            if(data.get(position).getwarnings().equalsIgnoreCase("Yes")&& position==0){

                holder.warning.setVisibility(View.VISIBLE);
            }else{

                holder.warning.setVisibility(View.GONE);
            }
            holder.Rl_left.setVisibility(View.VISIBLE);
            holder.Rl_right.setVisibility(View.GONE);
            holder.myRightTXTTime.setVisibility(View.GONE);
            holder.Tv_leftMessage.setText(data.get(position).getMessage().trim());
            if (!data.get(position).getDate().equals("")) {
                holder.myLeftTXTTime.setVisibility(View.VISIBLE);
                String[] aSplitDateStr = data.get(position).getDate().split(", ");
                holder.myLeftTXTTime.setText(aSplitDateStr[1]);
            } else {
                holder.myLeftTXTTime.setVisibility(View.GONE);
            }
        } else if (type.equalsIgnoreCase("TYPING") && message.equalsIgnoreCase("")) {
            holder.Rl_left.setVisibility(View.GONE);
            holder.Rl_right.setVisibility(View.GONE);
            holder.myLeftTXTTime.setVisibility(View.GONE);
            holder.myRightTXTTime.setVisibility(View.GONE);
            // holder.Tv_leftMessage.setText(data.get(position).getMessage());
        }

        holder.warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
                builder.setMessage(context.getResources().getString(R.string.warning1));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });

        return view;
    }
}
