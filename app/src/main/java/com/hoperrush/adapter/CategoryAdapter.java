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
import com.hoperrush.pojo.CategoryPojo;
import com.hoperrush.utils.RoundedCurveTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Casperon Technology on 12/19/2015.
 */
public class CategoryAdapter extends BaseAdapter {

    private ArrayList<CategoryPojo> data;
    private LayoutInflater mInflater;
    private Context context;
    private int lastPosition = -1;

    public CategoryAdapter(Context c, ArrayList<CategoryPojo> d) {
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
            view = mInflater.inflate(R.layout.homepage_single, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.category_single_title);
            holder.image = (ImageView) view.findViewById(R.id.category_single_imageView);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

         Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        view.startAnimation(animation);
        lastPosition = position;

        Animation animFadeOut = AnimationUtils.loadAnimation(context,R.anim.fade_in);
        holder.title.startAnimation(animFadeOut);
        holder.title.setText(data.get(position).getCat_name());
        //Picasso.with(context).invalidate(data.get(position).getCat_image());
        Picasso.with(context).load(data.get(position).getCat_image()).transform(new RoundedCurveTransformation(10, 0)).fit().into(holder.image);
        return view;
    }
}



