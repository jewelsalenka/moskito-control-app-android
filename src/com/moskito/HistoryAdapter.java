package com.moskito;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.example.moskito_control_app_android.R;
import com.stub.entity.HistoryItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * User: Olenka Shemshey
 * Date: 25.07.13
 */
public class HistoryAdapter extends BaseExpandableListAdapter {
    private List<HistoryItem> mHistory;
    private final Context context;

    public HistoryAdapter(Context context, List<HistoryItem> history) {
        this.context = context;
        if (history != null) {
            this.mHistory = history;
        } else {
            this.mHistory = new ArrayList<HistoryItem>();
        }
    }

    @Override
    public int getGroupCount() {
        return mHistory.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mHistory.get(groupPosition).getMessages().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mHistory.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mHistory.get(groupPosition).getMessages().get(childPosition);
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.history_list_item, null);
        }
        TextView dateView = (TextView) convertView.findViewById(R.id.change_date);
        TextView componentNameView = (TextView) convertView.findViewById(R.id.server_name);
        View oldStateView = convertView.findViewById(R.id.history_old_state);
        View newStateView = convertView.findViewById(R.id.history_new_state);

        HistoryItem historyItem = (HistoryItem) getGroup(groupPosition);
        String date = new SimpleDateFormat("dd MMM HH:mm:ss", Locale.ENGLISH).format(historyItem.getDate());
        dateView.setText(date);
        componentNameView.setText(historyItem.getComponentName());
        Drawable oldState = context.getResources().getDrawable(historyItem.getOldColorId());
        oldStateView.setBackgroundDrawable(oldState);
        Drawable newState = context.getResources().getDrawable(historyItem.getNewColorId());
        newStateView.setBackgroundDrawable(newState);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.charts_child_layout, null);
        }
        ((TextView) convertView.findViewById(R.id.history_info_text)).setText(mHistory.get(groupPosition).getMessages().get
                (childPosition));
        String newString = new SimpleDateFormat("HH:mm:ss").format(mHistory.get(groupPosition).getDate());
        ((TextView) convertView.findViewById(R.id.info_date)).setText(newString);
        convertView.findViewById(R.id.info_date);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
