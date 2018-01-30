package com.hoperrush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.pojo.ChatListPojo;
import com.hoperrush.core.widgets.RoundedImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Casperon Technology on 2/3/2016.
 */
public class ChatListAdapter extends BaseAdapter {

    private ArrayList<ChatListPojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public ChatListAdapter(Context c, ArrayList<ChatListPojo> d) {
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
        private TextView userName, message, time;
        private RoundedImageView Iv_userImage;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.chat_list_single, parent, false);
            holder = new ViewHolder();
            holder.userName = (TextView) view.findViewById(R.id.chat_list_single_userName_textView);
            holder.message = (TextView) view.findViewById(R.id.chat_list_single_message_textView);
            holder.time = (TextView) view.findViewById(R.id.chat_list_single_time_textView);
            holder.Iv_userImage = (RoundedImageView) view.findViewById(R.id.chat_list_single_userImageView);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.userName.setText(data.get(position).getUserName());
        holder.message.setText(data.get(position).getMessage());
        holder.time.setText(data.get(position).getMessageTime());
        Picasso.with(context).load(data.get(position).getUserImage()).error(R.drawable.placeholder_icon)
                .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).fit().into(holder.Iv_userImage);

        return view;
    }
}

