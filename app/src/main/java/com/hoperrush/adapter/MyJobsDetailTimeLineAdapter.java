package com.hoperrush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.pojo.MyJobDetailTimeLinePojo;

import java.util.ArrayList;

/**
 * Casperon Technology on 1/21/2016.
 */
public class MyJobsDetailTimeLineAdapter extends BaseAdapter {

    private ArrayList<MyJobDetailTimeLinePojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public MyJobsDetailTimeLineAdapter(Context c, ArrayList<MyJobDetailTimeLinePojo> d) {
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
        private TextView status, date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.myjobs_detail_time_line_single, parent, false);
            holder = new ViewHolder();
            holder.status = (TextView) view.findViewById(R.id.myJob_detail_status_single_status_textView);
            holder.date = (TextView) view.findViewById(R.id.myJob_detail_status_single_date_textView);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.status.setText(data.get(position).getTitle());
        holder.date.setText(data.get(position).getDate() + "\n" + data.get(position).getTime());
        return view;
    }
}


