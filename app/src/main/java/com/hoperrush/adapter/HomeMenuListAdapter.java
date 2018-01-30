package com.hoperrush.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.app.LogInPage;
import com.hoperrush.app.RegisterPage;
import com.hoperrush.core.widgets.CircularImageView;
import com.hoperrush.utils.CurrencySymbolConverter;
import com.hoperrush.utils.SessionManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Casperon Technology on 12/10/2015.
 */
public class HomeMenuListAdapter extends BaseAdapter {
    Context context;
    String[] mTitle;
    int[] mIcon;
    LayoutInflater inflater;
    View itemView;
    public SessionManager session;


    public HomeMenuListAdapter(Context context, String[] title, int[] icon) {
        this.context = context;
        this.mTitle = title;
        this.mIcon = icon;
    }

    @Override
    public int getCount() {
        return mTitle.length;
    }

    @Override
    public Object getItem(int position) {
        return mTitle[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        // Declare Variables
        TextView txtTitle, profile_name, mobile_number, walletMoney;
        CircularImageView profile_icon;
        ImageView imgIcon;
        RelativeLayout general_layout, profile_layout, signinbefore_layout, siginin_afterlogin;
        View drawer_view;
        RelativeLayout before_login;
        RelativeLayout login_text;
        session = new SessionManager(context);
        View drawer_list_item_view1;
        TextView register,signin;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = inflater.inflate(R.layout.navigation_drawer_single, parent, false);

        drawer_list_item_view1=(View)itemView.findViewById(R.id.drawer_list_item_view);
        txtTitle = (TextView) itemView.findViewById(R.id.title);
        walletMoney = (TextView) itemView.findViewById(R.id.drawer_item_list_wallet_money);
        imgIcon = (ImageView) itemView.findViewById(R.id.icon);
        profile_name = (TextView) itemView.findViewById(R.id.profile_name);
        mobile_number = (TextView) itemView.findViewById(R.id.profile_mobile_number);
        profile_icon = (CircularImageView) itemView.findViewById(R.id.profile_icon);
        general_layout = (RelativeLayout) itemView.findViewById(R.id.drawer_list_item_normal_layout);
        profile_layout = (RelativeLayout) itemView.findViewById(R.id.drawer_list_item_profile_layout);
        signinbefore_layout = (RelativeLayout) itemView.findViewById(R.id.layout_signIn_beforelogin);
        siginin_afterlogin = (RelativeLayout) itemView.findViewById(R.id.layout_after_login);

        register = (TextView) itemView.findViewById(R.id.register);
        signin = (TextView) itemView.findViewById(R.id.sign);




        if(!session.isLoggedIn()){
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, RegisterPage.class);
                    intent.putExtra("IntentClass", "1");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });
            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profile_intent = new Intent(context, LogInPage.class);
                    profile_intent.putExtra("IntentClass", "1");
                    profile_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(profile_intent);

                }
            });
        }




        if (session.isLoggedIn()) {
            if (position == 0) {
                HashMap<String, String> user = session.getUserDetails();
                String User_fullName = user.get(SessionManager.KEY_USERNAME);
                String User_Image = user.get(SessionManager.KEY_USER_IMAGE);
                String User_Country_Code = user.get(SessionManager.KEY_COUNTRY_CODE);
                String User_Mobile = user.get(SessionManager.KEY_PHONE_NUMBER);

                profile_layout.setVisibility(View.VISIBLE);
                general_layout.setVisibility(View.GONE);
                siginin_afterlogin.setVisibility(View.VISIBLE);
                signinbefore_layout.setVisibility(View.GONE);
                imgIcon.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                drawer_list_item_view1.setVisibility(View.VISIBLE);
                Picasso.with(context).load(User_Image).error(R.drawable.placeholder_icon)
                        .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(profile_icon);
                profile_name.setText(User_fullName);
                mobile_number.setText("(" + User_Country_Code + ")" + User_Mobile);

            } else {
                if (position == 3) {
                    walletMoney.setVisibility(View.VISIBLE);
                } else {
                    walletMoney.setVisibility(View.GONE);
                }

                profile_layout.setVisibility(View.GONE);
                general_layout.setVisibility(View.VISIBLE);
                imgIcon.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                drawer_list_item_view1.setVisibility(View.VISIBLE);
                HashMap<String, String> amount = session.getWalletDetails();
                String wallet_money = amount.get(SessionManager.KEY_WALLET_AMOUNT);
                String currencyCode = amount.get(SessionManager.KEY_CURRENCY_CODE);
                String currencySymbol = CurrencySymbolConverter.getCurrencySymbol(currencyCode);

                imgIcon.setImageResource(mIcon[position]);
                txtTitle.setText(mTitle[position]);
                walletMoney.setText(currencySymbol + wallet_money);
            }
        } else {
            if (position == 0) {
                profile_layout.setVisibility(View.VISIBLE);
                general_layout.setVisibility(View.GONE);

                signinbefore_layout.setVisibility(View.VISIBLE);
                siginin_afterlogin.setVisibility(View.GONE);
                drawer_list_item_view1.setVisibility(View.GONE);
                imgIcon.setVisibility(View.GONE);
                txtTitle.setVisibility(View.GONE);

            }
            else {

                profile_layout.setVisibility(View.GONE);
                general_layout.setVisibility(View.VISIBLE);
            }

//             imgIcon.setImageResource(mIcon[position]);
//             txtTitle.setText(mTitle[position]);
        }

        return itemView;
    }







}
