package com.hoperrush.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.app.ChatPage;
import com.hoperrush.app.ProvidersList;
import com.hoperrush.pojo.ProvidersListPojo;
import com.hoperrush.utils.SessionManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user88 on 6/24/2016.
 */
public class ProvidersListAdapter extends BaseAdapter {

    private ArrayList<ProvidersListPojo> data;
    private LayoutInflater mInflater;
    private Activity context;
    private int lastPosition = -1;
    public String Srating = "";

    private String StrTaskerId = "";

    private String sUserID = "";
    private SessionManager sessionManager;


    public ProvidersListAdapter(Activity c, ArrayList<ProvidersListPojo> d) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
        sessionManager = new SessionManager(context);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USER_ID);
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
        private TextView Tv_providername, Tv_provider_companyname, Tv_Available, Tv_mincost, Tv_hourlycost;
        private ImageView provider_img;
        private RatingBar provider_rating;
        private Button Bt_Chat, Bt_confirmBook;
        private TextView reviews,radius;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.providers_list_single, parent, false);
            holder = new ViewHolder();
            holder.Tv_providername = (TextView) view.findViewById(R.id.providername);
            holder.Tv_provider_companyname = (TextView) view.findViewById(R.id.provider_company_name);
            holder.provider_img = (ImageView) view.findViewById(R.id.providerimg);
            holder.provider_rating = (RatingBar) view.findViewById(R.id.provider_rating);
            holder.Tv_Available = (TextView) view.findViewById(R.id.provider_available);
            holder.Bt_Chat = (Button) view.findViewById(R.id.chat_button);
            holder.Bt_confirmBook = (Button) view.findViewById(R.id.confirmbook_button);
            holder.Tv_mincost = (TextView) view.findViewById(R.id.provider_mincost);
            holder.Tv_hourlycost = (TextView) view.findViewById(R.id.provider_percost);
            holder.reviews=(TextView)view.findViewById(R.id.reviews);
            holder.radius=(TextView)view.findViewById(R.id.radius);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }


        holder.Tv_providername.setText(data.get(position).getProvider_name());
        holder.Tv_provider_companyname.setText(data.get(position).getProvider_company());
        holder.Tv_Available.setText(data.get(position).getProvider_availble());
        holder.Tv_mincost.setText(data.get(position).getProvider_mincost());
        holder.Tv_hourlycost.setText(data.get(position).getHourly_rate());
        holder.reviews.setText(" reviews" +" " +data.get(position).getReviews());
        holder.radius.setText("~"+ " " +data.get(position).getRadius());



     /*   if (data.get(position).getProvider_availble().equalsIgnoreCase("Yes")){

            holder.Bt_Chat.setBackgroundColor(Color.parseColor("#2fb327"));
            holder.Bt_Chat.setTextColor(Color.parseColor("#ffffff"));

        }else {

            holder.Bt_Chat.setBackgroundColor(Color.parseColor("#cdcdcd"));
            holder.Bt_Chat.setTextColor(Color.parseColor("#ffffff"));
        }

*/


        //  Picasso.with(context).load(data.get(position).getProvider_image()).transform(new RoundedCurveTransformation(10, 0)).fit().into(holder.provider_img);


        Picasso.with(context).load(data.get(position).getProvider_image()).error(R.drawable.placeholder_icon)
                .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.provider_img);

        holder.provider_rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Srating = String.valueOf(rating);
                data.get(position).setProvider_rating(Srating);

            }
        });


        holder.provider_rating.setRating(Float.parseFloat(data.get(position).getProvider_rating()));

        holder.Bt_Chat.setOnClickListener(new onChatClickListener(position));

        holder.Bt_confirmBook.setOnClickListener(new onCallClickListener(position));

        return view;
    }


    private class onCallClickListener implements View.OnClickListener {
        int mPosition;

        private onCallClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {

            //SocketHandler.getInstance(context).getSocketManager().createRoom(""+data.get(mPosition).getTaskerId());

           /* if (StrTaskerId.length()==0 ){
                StrTaskerId = data.get(mPosition).getTaskerId();

                SocketHandler.getInstance(context).getSocketManager().createRoom(""+data.get(mPosition).getTaskerId());
            }else{

            if (StrTaskerId.equalsIgnoreCase(data.get(mPosition).getTaskerId())){
                SocketHandler.getInstance(context).getSocketManager().createSwitchRoom(""+data.get(mPosition).getTaskerId());
            }else{
                SocketHandler.getInstance(context).getSocketManager().createRoom(""+data.get(mPosition).getTaskerId());
            }

            }*/

            ProvidersList providers = (ProvidersList) context;
            providers.bookJob_Request(context, data.get(mPosition).getTaskerId());
        }
    }


    private class onChatClickListener implements View.OnClickListener {
        int mPosition;

        private onChatClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {

            System.out.println("chat-------------clik");
            Intent intent = new Intent(context, ChatPage.class);
            intent.putExtra("TaskerId", data.get(mPosition).getTaskerId());
            intent.putExtra("TaskId", data.get(mPosition).getTaskId());
            context.startActivity(intent);


        }
    }


}
