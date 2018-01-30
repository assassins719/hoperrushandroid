package com.hoperrush.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.app.Reviewimageviewclass;
import com.hoperrush.core.widgets.RoundedImageView;
import com.hoperrush.pojo.Myprofile_Reviwes_Pojo;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by user88 on 1/6/2016.
 */
public class MyProfile_Reviwes_Adapter extends BaseAdapter {
    private ArrayList<Myprofile_Reviwes_Pojo> data;
    private LayoutInflater mInflater;
    private Activity context;

    public MyProfile_Reviwes_Adapter(Activity c, ArrayList<Myprofile_Reviwes_Pojo> d) {
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

    public class ViewHolder {
        private TextView Tv_profile_name, Tv_description_reviwe, Tv_rating_time;
        private RatingBar profile_rating;
        private RoundedImageView profile_img;
        RelativeLayout reviewimageclick;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.taskerreview, parent, false);
            holder = new ViewHolder();
           // holder.reviewimageclick = (RelativeLayout) view.findViewById(R.id.reviewimage);
            holder.Tv_profile_name = (TextView) view.findViewById(R.id.profile_review_name);
            holder.profile_rating = (RatingBar) view.findViewById(R.id.layout_inflate_review_list_item_RGB);
            holder.profile_img = (RoundedImageView) view.findViewById(R.id.reviwes_profileimg);
            holder.Tv_description_reviwe = (TextView) view.findViewById(R.id.profile_review_description);
            holder.Tv_rating_time = (TextView) view.findViewById(R.id.profile_rating_time);
            view.setTag(holder);

        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.Tv_profile_name.setText(data.get(position).getName());
         holder.profile_rating.setRating(Float.parseFloat(data.get(position).getRating()));
        holder.Tv_description_reviwe.setText(data.get(position).getReviwe_description());
        holder.Tv_rating_time.setText(data.get(position).getRating_time());

        String rattingimage = data.get(position).getratingimage();

        Picasso.with(context).load(String.valueOf(data.get(position).getProfilimg())).placeholder(R.drawable.nouserimg).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.profile_img);

        return view;
    }


    private class Reviewimageclick implements View.OnClickListener {
        int position;

        private Reviewimageclick(int position) {

            this.position = position;
        }

        @Override
        public void onClick(View view) {

            String imageposition = data.get(position).getratingimage();

            Intent i = new Intent(context, Reviewimageviewclass.class);
            i.putExtra("reviewimage", imageposition);
            context.startActivity(i);

        }
    }


}
