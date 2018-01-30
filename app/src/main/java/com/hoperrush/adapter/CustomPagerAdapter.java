package com.hoperrush.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.hoperrush.Interface.MapFragmentcall;
import com.hoperrush.R;
import com.hoperrush.app.ChatPage;
import com.hoperrush.app.PartnerProfilePage;
import com.hoperrush.core.socket.ChatMessageService;
import com.hoperrush.fragment.Fragment_New_Map_HomePage;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.MoreTaskerarray;
import com.hoperrush.utils.CurrencySymbolConverter;
import com.hoperrush.utils.SessionManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user145 on 8/3/2017.
 */
public class CustomPagerAdapter extends PagerAdapter {
    private Context myContext;
    ArrayList<MoreTaskerarray> moretasker_list;
    private LayoutInflater myLayoutInflater;
    private SessionManager sessionManager;
    Marker mark;
    String str_Taskid="";
    Dialog moreAddressDialog;
    String myCurrencySymbol="";
    String current_lat="";
    String current_long="";
    private MapFragmentcall callmetod;


    public CustomPagerAdapter(Context myContext, ArrayList<MoreTaskerarray> moretasker_list, Marker mark, String str_Taskid, Dialog moreAddressDialog, String current_lat, String current_long, Fragment_New_Map_HomePage fragment_new_map_homePage) {
        this.myContext=myContext;
        this.moretasker_list=moretasker_list;
        this.mark=mark;
        this.str_Taskid=str_Taskid;
        this.moreAddressDialog=moreAddressDialog;
        this.current_lat=current_lat;
        this.current_long=current_long;
        callmetod=fragment_new_map_homePage;
        myLayoutInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(myContext);
    }

    @Override
    public int getCount() {
        return moretasker_list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = myLayoutInflater.inflate(R.layout.map_tasker_select, container, false);
        TextView minicost = (TextView) itemView.findViewById(R.id.mini_cost);
        TextView hourcost = (TextView) itemView.findViewById(R.id.hour_cost);
        TextView username = (TextView) itemView.findViewById(R.id.user_name);
        ImageView userimage = (ImageView) itemView.findViewById(R.id.user_image);
        TextView address = (TextView) itemView.findViewById(R.id.tasker_address);
        ImageView tasker_close = (ImageView) itemView.findViewById(R.id.tasker_close);
        RelativeLayout chat = (RelativeLayout) itemView.findViewById(R.id.chat);
        ImageView tasker_select = (ImageView) itemView.findViewById(R.id.tasker_select);
        RelativeLayout detail = (RelativeLayout) itemView.findViewById(R.id.tasker_det);
       // TextView chat_text = (TextView) itemView.findViewById(R.id.tasker_select_text);
        TextView detail_text = (TextView) itemView.findViewById(R.id.detail_text);
        TextView select_tasker = (TextView) itemView.findViewById(R.id.select_tasker);
        TextView unselect_tasker = (TextView) itemView.findViewById(R.id.unselect_tasker);
        RatingBar rating = (RatingBar) itemView.findViewById(R.id.rating_image);
        HashMap<String, String> aAmountMap = sessionManager.getWalletDetails();
        String aCurrencyCode = aAmountMap.get(SessionManager.KEY_CURRENCY_CODE);
        myCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(aCurrencyCode);

        username.setText(moretasker_list.get(position).getTitle());
        minicost.setText(" Hourly Cost  : " + " " + myCurrencySymbol + moretasker_list.get(position).getHourly_cost());
        address.setText(moretasker_list.get(position).getAddress());
        String tasker_rating=moretasker_list.get(position).getRating();
        rating.setRating(Float.parseFloat(tasker_rating));
        Picasso.with(myContext).load(moretasker_list.get(position).getUrlimage())
                .error(R.drawable.placeholder_icon)
                .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(userimage);

        chat.setOnClickListener(new ChatButtonClick(position));
        detail.setOnClickListener(new TaskerDetailpage(position));
        tasker_select.setOnClickListener(new TaskerSelect(position));
        tasker_close.setOnClickListener(new TaskerClose(position));
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private class ChatButtonClick implements View.OnClickListener {
        int position;
        public ChatButtonClick(int position) {
            this.position=position;
        }

        @Override
        public void onClick(View v) {
            ChatMessageService.tasker_id = "";
            ChatMessageService.task_id = "";
            Intent chat = new Intent(myContext, ChatPage.class);
            chat.putExtra("TaskerId", moretasker_list.get(position).getTasker_id());
            chat.putExtra("TaskId", str_Taskid);
            myContext.startActivity(chat);
            if(moreAddressDialog!=null){
                moreAddressDialog.dismiss();
            }

        }
    }

    private class TaskerDetailpage implements View.OnClickListener {
        int position;
        public TaskerDetailpage(int position) {
            this.position=position;
        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(myContext, PartnerProfilePage.class);
            sessionManager.putProvideID(moretasker_list.get(position).getTasker_id());
            sessionManager.putProvideScreenType(Iconstant.PROVIDER);
            i.putExtra("userid", Fragment_New_Map_HomePage.UserID);
            i.putExtra("task_id", str_Taskid);
            i.putExtra("address", moretasker_list.get(position).getAddress());
            i.putExtra("taskerid", moretasker_list.get(position).getTasker_id());
            i.putExtra("minimumamount", myCurrencySymbol + Fragment_New_Map_HomePage.minimum_amount);
            i.putExtra("hourlyamount", myCurrencySymbol + moretasker_list.get(position).getHourly_cost());
            i.putExtra("Page", "map_page");
            i.putExtra("lat", current_lat);
            i.putExtra("long", current_long);
            i.putExtra("location", Fragment_New_Map_HomePage.SselectedLocation);
            i.putExtra("city", Fragment_New_Map_HomePage.city);
            i.putExtra("state", Fragment_New_Map_HomePage.state);
            i.putExtra("postalcode", Fragment_New_Map_HomePage.postalCode);
            myContext.startActivity(i);
            if(moreAddressDialog!=null){
                moreAddressDialog.dismiss();
            }

        }
    }

    private class TaskerSelect implements View.OnClickListener {
        int position;
        public TaskerSelect(int position) {
            this.position=position;
        }

        @Override
        public void onClick(View v) {

            Fragment_New_Map_HomePage.taskerselect_status=true;
            callmetod.more_tasker_markershow(mark,moretasker_list.get(position).getTitle(),myContext,moretasker_list.get(position).getTasker_id(),str_Taskid,moretasker_list.get(position).getTitle());
            if (mark != null) {
                mark.showInfoWindow();
            } else {
                mark.hideInfoWindow();
            }
            if(moreAddressDialog!=null){
                moreAddressDialog.dismiss();
            }
        }
    }

    private class TaskerClose implements View.OnClickListener {
        int position;
        public TaskerClose(int position) {
            this.position=position;
        }

        @Override
        public void onClick(View v) {
            Fragment_New_Map_HomePage.taskerselect_status=false;
            Fragment_New_Map_HomePage.book_now_taskername="";
            Fragment_New_Map_HomePage.book_now_taskerid="";
            Fragment_New_Map_HomePage.book_now_taskid="";

            if(moreAddressDialog!=null){
                moreAddressDialog.dismiss();
            }
        }
    }
}