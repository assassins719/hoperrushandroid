package com.hoperrush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.pojo.CategoryDetailPojo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Casperon Technology on 1/5/2016.
 */
public class CategoriesDetailAdapter extends BaseAdapter {

    private ArrayList<CategoryDetailPojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public CategoriesDetailAdapter(Context c, ArrayList<CategoryDetailPojo> d) {
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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.categories_detail_single, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.category_detail_single_title);
            holder.image = (ImageView) view.findViewById(R.id.category_detail_single_imageView);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        Animation animFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        holder.title.startAnimation(animFadeOut);
        holder.title.setText(data.get(position).getCat_name());
        //Picasso.with(context).invalidate(data.get(position).getCat_image());
        Picasso.with(context).load(data.get(position).getIcon_normal()).fit().into(holder.image);
        return view;
    }
}
