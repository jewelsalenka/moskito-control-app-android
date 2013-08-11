package com.moskito;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.moskito_control_app_android.R;
import com.stub.entity.HistoryItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 25.07.13
 */
public class HistoryAdapter extends BaseAdapter {
    private List<HistoryItem> history;
    private final Context context;

    public HistoryAdapter(Context context, List<HistoryItem> history) {
        this.context = context;
        if (history != null){
            this.history = history;
        } else {
            this.history = new ArrayList<HistoryItem>();
        }
    }
    @Override
    public int getCount() {
        return history.size();
    }

    @Override
    public Object getItem(int position) {
        return history.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        if (rootView == null){
            rootView = LayoutInflater.from(context).inflate(R.layout.history_list_item_advanced, parent, false);
        }
        TextView dateView = (TextView) rootView.findViewById(R.id.change_date);
        TextView componentNameView = (TextView) rootView.findViewById(R.id.server_name);
        View oldStateView = rootView.findViewById(R.id.history_old_state);
        View newStateView = rootView.findViewById(R.id.history_new_state);

        HistoryItem historyItem = (HistoryItem) getItem(position);
        dateView.setText(getDateString(historyItem.getDate()));
        componentNameView.setText(historyItem.getComponent().getName());
        Drawable oldState = context.getResources().getDrawable(historyItem.getOldColorId());
        oldStateView.setBackgroundDrawable(oldState);
        Drawable newState = context.getResources().getDrawable(historyItem.getNewColorId());
        newStateView.setBackgroundDrawable(newState);

        RelativeLayout advancedLayout = (RelativeLayout) rootView.findViewById(R.id.item_history_advanced);
        if (rootView.isSelected()){
            advancedLayout.setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.info_text)).setText(historyItem.getInfo());
            ((TextView) rootView.findViewById(R.id.info_date)).setText(historyItem.getDate().toLocaleString());
            rootView.findViewById(R.id.info_date);
        } else {
            advancedLayout.setVisibility(View.GONE);
        }

        return rootView;
    }

    private String getDateString(Date date){
        return date.getDay() + " " + date.getMonth() + " "
                + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
    }
}
