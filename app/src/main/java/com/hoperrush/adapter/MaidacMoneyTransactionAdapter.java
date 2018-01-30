package com.hoperrush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.pojo.PlumbalMoneyTransactionPojo;

import java.util.ArrayList;


/**
 * Casperon Technology on 10/23/2015.
 */
public class MaidacMoneyTransactionAdapter extends BaseAdapter
{

    private ArrayList<PlumbalMoneyTransactionPojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public MaidacMoneyTransactionAdapter(Context c, ArrayList<PlumbalMoneyTransactionPojo> d)
    {
        context=c;
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
    public int getViewTypeCount()
    {
        return 1;
    }


    public class ViewHolder
    {
        private ImageView type_image;
        private TextView type_name;
        private TextView trans_price,title,date,balance;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;
        ViewHolder holder;
        if(convertView == null)
        {
            view = mInflater.inflate(R.layout.plumbal_money_transaction_single, parent, false);
            holder = new ViewHolder();
            holder.type_name = (TextView) view.findViewById(R.id.cabily_money_transaction_single_type_textview);
            holder.type_image = (ImageView) view.findViewById(R.id.cabily_money_transaction_single_type_imageview);
            holder.trans_price = (TextView) view.findViewById(R.id.cabily_money_transaction_single_price);
            holder.title = (TextView) view.findViewById(R.id.cabily_money_transaction_single_description);
            holder.date = (TextView) view.findViewById(R.id.cabily_money_transaction_single_date);
            holder.balance = (TextView) view.findViewById(R.id.cabily_money_transaction_single_balance_price);

            view.setTag(holder);
        }
        else
        {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }


            if(data.get(position).getTrans_type().equalsIgnoreCase("CREDIT"))
            {
                holder.type_name.setText(context.getResources().getString(R.string.plumbal_money_label_transaction_list_credit));
                holder.type_name.setTextColor(0xFF006301);
                holder.type_image.setImageResource(R.drawable.up_arrow);
            }
            else
            {
                holder.type_name.setText(context.getResources().getString(R.string.plumbal_money_label_transaction_list_debit));
                holder.type_name.setTextColor(0xFFcc0000);
                holder.type_image.setImageResource(R.drawable.down_arrow);
            }


            holder.trans_price.setText(data.get(position).getCurrencySymbol()+data.get(position).getTrans_amount());
            holder.title.setText(data.get(position).getTitle());
            holder.date.setText(data.get(position).getTrans_date());
            holder.balance.setText(context.getResources().getString(R.string.plumbal_money_label_transaction_list_balance)+" "+data.get(position).getCurrencySymbol()+data.get(position).getBalance_amount());


        return view;
    }
}


