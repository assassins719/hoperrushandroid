package com.hoperrush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.utils.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Prem Kumar on 10/7/2015.
 */
public class PlaceSearchAdapter extends BaseAdapter
{

    private ArrayList<String> data;
    private ImageLoader imageLoader;
    private LayoutInflater mInflater;
    private Context context;

    public PlaceSearchAdapter(Context c, ArrayList<String> d)
    {
        context=c;
        mInflater = LayoutInflater.from(context);
        data = d;
        imageLoader = new ImageLoader(context);
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
    public int getViewTypeCount()
    {
        return 1;
    }


    public class ViewHolder
    {
        private TextView name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;
        ViewHolder holder;
        if(convertView == null)
        {
            view = mInflater.inflate(R.layout.place_search_single, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.place_search_single_textview);

            view.setTag(holder);
        }
        else
        {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        holder.name.setText(data.get(position));

        return view;
    }
}
