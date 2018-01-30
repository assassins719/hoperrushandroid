package com.hoperrush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;

import com.hoperrush.R;
import com.hoperrush.core.widgets.CustomTextView;
import com.hoperrush.core.widgets.RoundedImageView;
import com.hoperrush.pojo.ReviewPojoInfo;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by user145 on 2/14/2017.
 */
public class ReviewListAdapter extends BaseAdapter {

    private Context myContext;
    private ArrayList<ReviewPojoInfo> myReviewInfoList;
    private LayoutInflater mInflater;


    public ReviewListAdapter(Context aContext, ArrayList<ReviewPojoInfo> aReviewInfoList) {
        this.myContext = aContext;
        this.myReviewInfoList = aReviewInfoList;
        mInflater = LayoutInflater.from(myContext);
    }

    @Override
    public int getCount() {
        return myReviewInfoList.size();
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
        private CustomTextView aNameTXT, aJobIdTXT, aCommentsTXT, aViewImageTXT, aTimeTXT, aCategoryTXT;
        private RoundedImageView aTaskerIMG;
        private RatingBar aRatingBar;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_inflate_review_list_item, parent, false);
            holder = new ViewHolder();
            holder.aNameTXT = (CustomTextView) convertView.findViewById(R.id.layout_inflate_review_list_item_TXT_name);
            holder.aJobIdTXT = (CustomTextView) convertView.findViewById(R.id.layout_inflate_review_list_item_TXT_Jobid);
            holder.aCommentsTXT = (CustomTextView) convertView.findViewById(R.id.layout_inflate_review_list_item_TXT_comments);
            holder.aTaskerIMG = (RoundedImageView) convertView.findViewById(R.id.layout_inflate_review_list_item_IMG_profile);
            holder.aRatingBar = (RatingBar) convertView.findViewById(R.id.layout_inflate_review_list_item_RGB);
            holder.aCategoryTXT = (CustomTextView) convertView.findViewById(R.id.layout_inflate_review_list_item_TXT_category);


            holder.aViewImageTXT = (CustomTextView) convertView.findViewById(R.id.layout_inflate_review_list_item_TXT_viewimage);
            holder.aTimeTXT = (CustomTextView) convertView.findViewById(R.id.layout_inflate_review_list_item_TXT_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.aNameTXT.setText(myReviewInfoList.get(position).getReviewTasker());
        holder.aRatingBar.setRating(Float.parseFloat(myReviewInfoList.get(position).getReviewRating()));
        holder.aCommentsTXT.setText(myReviewInfoList.get(position).getReviewComments());
        holder.aJobIdTXT.setText(myReviewInfoList.get(position).getReviewBookingId() + " - ");
        holder.aCategoryTXT.setText(myReviewInfoList.get(position).getReviewCategory());
        holder.aTimeTXT.setText(myReviewInfoList.get(position).getReviewDate());

        Picasso.with(myContext).load(String.valueOf(myReviewInfoList.get(position).getReviewTaskerImage()))
                .placeholder(R.drawable.nouserimg).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.aTaskerIMG);

        if (myReviewInfoList.get(position).getReviewImage().equals("")) {
            holder.aViewImageTXT.setVisibility(View.GONE);
        } else {
            holder.aViewImageTXT.setVisibility(View.VISIBLE);
        }
        holder.aViewImageTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent aReviewIntent = new Intent(myContext, ReviewImageActivity.class);
//                aReviewIntent.putExtra("IMAGE_URL", myReviewInfoList.get(position).getReviewImage());
//                myContext.startActivity(aReviewIntent);
            }
        });
        return convertView;
    }
}
