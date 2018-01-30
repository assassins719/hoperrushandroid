package com.hoperrush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.pojo.RatingPojo;

import java.util.ArrayList;

/**
 * Casperon Technology on 1/25/2016.
 */
public class RatingAdapter extends BaseAdapter {

    private ArrayList<RatingPojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public RatingAdapter(Context c, ArrayList<RatingPojo> d) {
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
        private TextView title;
        private RatingBar rating;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.rating_single, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.rating_single_title);
            holder.rating = (RatingBar) view.findViewById(R.id.rating_single_ratingBar);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.title.setText(data.get(position).getRatingName());

        holder.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                data.get(position).setRatingCount(String.valueOf(rating));
            }
        });

        return view;
    }
}
