package com.moskito;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.moskito_control_app_android.R;
import com.stub.entity.Change;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 25.07.13
 */
public class HistoryAdapter extends BaseAdapter {
    private List<Change> history;
    private final Context context;

    public HistoryAdapter(Context context, List<Change> servers) {
        this.context = context;
        if (servers != null){
            this.history = servers;
        } else {
            this.history = new ArrayList<Change>();
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.history_list_item, parent, false);

        TextView dateView = (TextView) rootView.findViewById(R.id.change_date);
        TextView serverNameView = (TextView) rootView.findViewById(R.id.server_name);
        View oldStateView = rootView.findViewById(R.id.history_old_state);
        View newStateView = rootView.findViewById(R.id.history_new_state);

        Change change = (Change) getItem(position);
        dateView.setText(getDateString(change.getDate()));
        serverNameView.setText(change.getServer().getName());
        Drawable oldState = context.getResources().getDrawable(change.getOldColorId());
        oldStateView.setBackground(oldState);
        Drawable newState = context.getResources().getDrawable(change.getNewColorId());
        newStateView.setBackground(newState);


        return rootView;
    }

    private String getDateString(Date date){
        return date.getDay() + " " + date.getMonth() + " "
                + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
    }
}
