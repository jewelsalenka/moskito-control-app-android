package com.moskito;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.moskito_control_app_android.R;
import com.stub.entity.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 14.07.13
 */
public class ComponentAdapter extends BaseExpandableListAdapter {
    private List<Component> mComponents;
    private final Context mContext;

    public ComponentAdapter(Context context, List<Component> components) {
        mContext = context;
        if (components != null){
            mComponents = components;
        } else {
            mComponents = new ArrayList<Component>();
        }
    }

    @Override
    public int getGroupCount() {
        return mComponents.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Component getGroup(int groupPosition) {
        return mComponents.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition << 16 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View rootView = convertView;
        if (rootView == null){
            rootView = LayoutInflater.from(mContext).inflate(R.layout.component_parent_layout, parent, false);
        }
        View componentStateView = rootView.findViewById(R.id.server_color_state);
        TextView appName = (TextView) rootView.findViewById(R.id.server_name);
        Component component = getGroup(groupPosition);
        Drawable color = mContext.getResources().getDrawable(component.getState().getColorDrawableId());
        componentStateView.setBackgroundDrawable(color);
        appName.setText(component.getName());
        return rootView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View rootView = convertView;
        if (rootView == null) {
            rootView = LayoutInflater.from(mContext).inflate(R.layout.component_child_layout, parent, false);
        }
        Component component = getGroup(groupPosition);
        ((TextView) rootView.findViewById(R.id.info_text)).setText(component.getInfo());
        String date = new SimpleDateFormat("dd MMM HH:mm:ss").format(component.getDate());
        ((TextView) rootView.findViewById(R.id.info_date)).setText(date);
        return rootView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
