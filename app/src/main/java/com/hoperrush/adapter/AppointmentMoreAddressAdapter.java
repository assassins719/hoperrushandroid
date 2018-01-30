package com.hoperrush.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.app.NewAppointmentpage;
import com.hoperrush.core.gps.GPSTracker;
import com.hoperrush.pojo.AddressListPojo;
import com.hoperrush.utils.SessionManager;

import java.util.ArrayList;

/**
 * Casperon Technology on 1/11/2016.
 */
public class AppointmentMoreAddressAdapter extends BaseAdapter {

    private ArrayList<AddressListPojo> data;
    private LayoutInflater mInflater;
    private Context context;
    private SessionManager sessionManager;
    private GPSTracker gps;
    String sDisplayAddress = "";

    public AppointmentMoreAddressAdapter(Context c, ArrayList<AddressListPojo> d) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
        sessionManager = new SessionManager(context);


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
        private RelativeLayout Rl_deleteAddress;
        private RelativeLayout Rl_isAddressSelected;
        private ImageView check_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.appointment_more_address_single, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.appointment_more_address_single_addressText);
            holder.Rl_deleteAddress = (RelativeLayout) view.findViewById(R.id.appointment_more_address_single_delete_layout);
            holder.Rl_isAddressSelected = (RelativeLayout) view.findViewById(R.id.appointment_more_address_single_isAddressSelected_layout);
            holder.check_id=(ImageView)view.findViewById(R.id.check_id);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        if (data.get(position).getLandmark().equals("")) {


            if (data.get(position).getLocality().equals("")) {
                sDisplayAddress = data.get(position).getName() + "\n" + data.get(position).getStreet();
//                        + "\n" + data.get(position).getCity() + "\n" + "Zipcode"
//                        + "-" + data.get(position).getZipCode();

            } else {

                sDisplayAddress = data.get(position).getName() + "\n" + data.get(position).getStreet();
//                        + "\n" + data.get(position).getCity() + "\n" + data.get(position).getLocality() + "\n" + "Zipcode"
//                        + "-" + data.get(position).getZipCode();

            }

        } else if (data.get(position).getLocality().equals("")) {

            sDisplayAddress = data.get(position).getName() + "\n" + data.get(position).getStreet();
//                    + "\n" + data.get(position).getCity() + "Zipcode"
//                    + "-" + data.get(position).getZipCode() + "\n"
//                    + context.getResources().getString(R.string.appointment_label_landmark)
//                    + " " + data.get(position).getLandmark();
        } else {

            sDisplayAddress = data.get(position).getName() + "\n" + data.get(position).getStreet();
//                    + "\n" + data.get(position).getCity() + "\n" + data.get(position).getLocality() + "\n" + "Zipcode"
//                    + "-" + data.get(position).getZipCode() + "\n"
//                    + context.getResources().getString(R.string.appointment_label_landmark)
//                    + " " + data.get(position).getLandmark();

        }

        System.out.println("sDisplayAddress---------" + sDisplayAddress);

        holder.title.setText(data.get(position).getAddress());

        String status=data.get(position).getAddressstatus();

        if (data.get(position).isAddressSelected()) {
            holder.Rl_isAddressSelected.setBackgroundColor(Color.parseColor("#f1f1f1"));
            //holder.check_id.setVisibility(View.VISIBLE);
        } else {

            holder.Rl_isAddressSelected.setBackgroundColor(Color.parseColor("#ffffff"));
           // holder.check_id.setVisibility(View.GONE);
        }


        System.out.println("name-----------" + data.get(position).getName());
        System.out.println("street-----------" + data.get(position).getStreet());
        System.out.println("getCity-----------" + data.get(position).getCity());
        System.out.println("getLocality-----------" + data.get(position).getLocality());
        System.out.println("getCountry_code-----------" + data.get(position).getCountry_code());

        holder.Rl_deleteAddress.setOnClickListener(new deleteAddressOnClick(position));

        return view;
    }

    private class deleteAddressOnClick implements View.OnClickListener {
        private int mPosition = 0;

        private deleteAddressOnClick(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            NewAppointmentpage appointmentPage = (NewAppointmentpage) context;
            appointmentPage.deleteAddressDialog(data.get(mPosition).getAddress_name());
        }
    }
}
