package com.hoperrush.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.pojo.CategoryPojo;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by user145 on 2/21/2017.
 */
public class Map_main_category_adapter  extends BaseAdapter {

    private ArrayList<CategoryPojo> data;
    private LayoutInflater mInflater;
    private Context context;
    private int lastPosition = -1;

    public Map_main_category_adapter(Context c, ArrayList<CategoryPojo> d) {
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
        private ImageView image;
        private ImageView check_makr_image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.map_single_listview_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.main_category_text);
            holder.image = (ImageView) view.findViewById(R.id.main_category_image);
            holder.check_makr_image=(ImageView)view.findViewById(R.id.check_mark_image);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        boolean type=data.get(position).isCategorySelected();

        if (data.get(position).isCategorySelected()) {
            holder.title.setTextColor(Color.parseColor("#526CA5"));

            Picasso.with(context).load(data.get(position).getIcon_normal())
                    .error(R.drawable.placeholder_icon)
                    .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.image);
        }
       else{
            holder.title.setTextColor(Color.parseColor("#000000"));
            Picasso.with(context).load(data.get(position).getCat_image())
                    .error(R.drawable.placeholder_icon)
                    .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.image);
        }



        holder.title.setText(data.get(position).getCat_name());
//        Picasso.with(context).load(data.get(position).getCat_image())
//                .error(R.drawable.placeholder_icon)
//                .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.image);
        return view;
    }
}




