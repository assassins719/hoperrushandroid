package com.hoperrush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;

import com.hoperrush.R;
import com.hoperrush.core.widgets.CustomTextView;
import com.hoperrush.pojo.NotificationPojoInfo;

import java.util.ArrayList;

/**
 * Created by user145 on 2/14/2017.
 */
public class NotificationExpandListAdapter extends BaseExpandableListAdapter {
    private Context myContext;
    private ArrayList<NotificationPojoInfo> myNotificationInfo;

    public NotificationExpandListAdapter(Context aContext, ArrayList<NotificationPojoInfo> aNotificationList) {
        this.myContext = aContext;
        this.myNotificationInfo = aNotificationList;
    }

    @Override
    public int getGroupCount() {
        return myNotificationInfo.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return myNotificationInfo.get(groupPosition).getNotificationMessageInfo().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return myNotificationInfo.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) myContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.layout_inflate_notifcation_group_list_item, null);
        }

        CustomTextView aCategoryTXT = (CustomTextView) convertView
                .findViewById(R.id.layout_inflate_notification_group_list_item_TXT_title);

        ImageView aArrowIMG = (ImageView) convertView
                .findViewById(R.id.layout_inflate_notification_group_list_item_IMG_arrow);

        aCategoryTXT.setText(myNotificationInfo.get(groupPosition).getNotificationBookingId()
                + " - " + myNotificationInfo.get(groupPosition).getNotificationCategory());
        if (isExpanded) {
            aArrowIMG.setImageResource(R.drawable.icon_down_arrow_expand);
        } else {
            aArrowIMG.setImageResource(R.drawable.icon_right_arrow_expand);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) myContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.layout_inflate_notifcation_child_list_item, null);
        }

        CustomTextView aChildCategoryTXT = (CustomTextView) convertView
                .findViewById(R.id.layout_inflate_notification_child_list_item_TXT_title);

        CustomTextView aTimeTXT = (CustomTextView) convertView
                .findViewById(R.id.layout_inflate_notification_child_list_item_TXT_time);

        aChildCategoryTXT.setText(myNotificationInfo.get(groupPosition).getNotificationMessageInfo().get(childPosition).getNotificationMessage());
        aTimeTXT.setText(myNotificationInfo.get(groupPosition).getNotificationMessageInfo().get(childPosition).getNotificationMessageCreatedAt());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
