package com.hoperrush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.pojo.CitySelectionPojo;

import java.util.ArrayList;

/**
 * Casperon Technology on 12/9/2015.
 */
public class CitySelectionAdapter extends BaseAdapter {

    private ArrayList<CitySelectionPojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public CitySelectionAdapter(Context c, ArrayList<CitySelectionPojo> d) {
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
        private TextView name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.city_selection_single, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.city_selection_single_textView);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(data.get(position).getLocationName());
        return view;
    }
}


