package com.hoperrush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.hoperrush.R;
import com.hoperrush.pojo.Availabilitypojo;

import java.util.ArrayList;

/**
 * Created by user145 on 11/7/2016.
 */
public class Availabilityadapter extends BaseAdapter {

    private ArrayList<Availabilitypojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public Availabilityadapter(Context c, ArrayList<Availabilitypojo> d) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    public class ViewHolder {
        public ImageView messageStatus;
        private TextView day;
        private ImageView tick1;
        private ImageView tick2;
        private ImageView tick3;
        private RelativeLayout Rl_left, Rl_right;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.availabilitysingle, parent, false);
            holder = new ViewHolder();
            holder.day = (TextView) view.findViewById(R.id.days);
            holder.tick1 = (ImageView) view.findViewById(R.id.tick1);
            holder.tick2 = (ImageView) view.findViewById(R.id.tick2);
            holder.tick3 = (ImageView) view.findViewById(R.id.tick3);


            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();


            }


        String days=data.get(position).getDays();

        holder.day.setText(data.get(position).getDays());
        String mor=data.get(position).getMorning();

        if(mor.equalsIgnoreCase("true")||mor.equalsIgnoreCase("1")){
            holder.tick1.setImageResource(R.drawable.ic_single_tick);

        }else{
            holder.tick1.setImageResource(R.drawable.delete1);

        }
        String after= data.get(position).getAfternoon();
        if(after.equalsIgnoreCase("true")||after.equalsIgnoreCase("1")){
            holder.tick2.setImageResource(R.drawable.ic_single_tick);

        }else{
            holder.tick2.setImageResource(R.drawable.delete1);

        }
        String eve=data.get(position).getEvening();
        if(eve.equalsIgnoreCase("true")||eve.equalsIgnoreCase("1")) {
            holder.tick3.setImageResource(R.drawable.ic_single_tick);

        }else{
            holder.tick3.setImageResource(R.drawable.delete1);

        }

        return view;
    }
}