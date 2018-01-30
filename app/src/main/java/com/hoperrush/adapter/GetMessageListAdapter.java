package com.hoperrush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hoperrush.R;
import com.hoperrush.core.widgets.CustomTextView;
import com.hoperrush.pojo.MessageChatPojo;
import com.hoperrush.utils.RoundedCurveTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by CAS61 on 12/30/2016.
 */
public class GetMessageListAdapter extends BaseAdapter {
private ArrayList<MessageChatPojo> myInfoList;
private Context myContext;
private LayoutInflater mInflater;

public GetMessageListAdapter(Context aContext, ArrayList<MessageChatPojo> aInfoList) {
        this.myContext = aContext;
        mInflater = LayoutInflater.from(myContext);
        this.myInfoList = aInfoList;

        }

@Override
public int getCount() {
        return myInfoList.size();
        }

@Override
public Object getItem(int position) {
        return position;
        }

@Override
public long getItemId(int position) {
        return position;
        }

public class ViewHolder {
    private CustomTextView aOrderIdTXT, aTaskerNameTXT,jobcategory,date;
    private ImageView aTaskerIMG;
    ImageView green_dot;
}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_inflate_messagechat_list_item, parent, false);
            holder = new ViewHolder();
            holder.aOrderIdTXT = (CustomTextView) convertView.findViewById(R.id.job_id);
            holder.aTaskerNameTXT = (CustomTextView) convertView.findViewById(R.id.username);
            holder.aTaskerIMG = (ImageView) convertView.findViewById(R.id.layout_inflate_messagechat_list_item_IMG);
            holder.jobcategory=(CustomTextView)convertView.findViewById(R.id.job_category);
            holder.date=(CustomTextView)convertView.findViewById(R.id.date);
            holder.green_dot=(ImageView) convertView.findViewById(R.id.green_dot);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(myInfoList.get(position).getstatus().equalsIgnoreCase("1")){
            holder.green_dot.setVisibility(View.VISIBLE);

        }else{

            holder.green_dot.setVisibility(View.GONE);
        }

        holder.aOrderIdTXT.setText(myInfoList.get(position).getMessageBookingId());
        holder.aTaskerNameTXT.setText(myInfoList.get(position).getMessageTaskerNameId());
        holder.jobcategory.setText(myInfoList.get(position).getCategory());
        holder.date.setText(myInfoList.get(position).getdate());
        Picasso.with(myContext).load(myInfoList.get(position).getMessageTaskerImageId()).transform(new RoundedCurveTransformation(10, 0)).fit().into(holder.aTaskerIMG);
        return convertView;
    }
}
