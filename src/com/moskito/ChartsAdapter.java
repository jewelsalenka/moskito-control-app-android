package com.moskito;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.example.moskito_control_app_android.R;
import com.stub.entity.Chart;
import com.stub.entity.Line;

import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 27.10.13
 */
public class ChartsAdapter extends BaseExpandableListAdapter {
    private List<Chart> mCharts;
    private Context mContext;

    public ChartsAdapter(Context mContext, List<Chart> mCharts) {
        this.mCharts = mCharts;
        this.mContext = mContext;
    }

    @Override
    public int getGroupCount() {
        return mCharts.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCharts.get(groupPosition).getLines().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCharts.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCharts.get(groupPosition).getLines();
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
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.charts_parent_layout, null);
        }
        TextView chartNameView = (TextView) convertView.findViewById(R.id.charts_name);
        chartNameView.setText(mCharts.get(groupPosition).getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.charts_child_layout, null);
        }
        TextView lineNameView = (TextView) convertView.findViewById(R.id.line_name);
        Line line = mCharts.get(groupPosition).getLines().get(childPosition);
        lineNameView.setText(line.getName());
        View tick = convertView.findViewById(R.id.show_or_hide_line);
        if (line.isDrawable()){
            tick.setVisibility(View.VISIBLE);
        } else {
            tick.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}