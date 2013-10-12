package com.moskito;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.moskito_control_app_android.R;
import com.stub.entity.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class AppAdapter extends BaseAdapter {
    private List<Application> applications;
    private final Context context;
    private int currentApp = 0;
    private final Drawable arrow;
    private final Drawable activeArrow;

    public AppAdapter(Context context, List<Application> apps) {
        this.context = context;
        arrow = context.getResources().getDrawable(R.drawable.arrow_right);
        activeArrow = context.getResources().getDrawable(R.drawable.arrow_right_active);
        if (apps != null){
            this.applications = apps;
        } else {
            this.applications = new ArrayList<Application>();
        }
    }
    @Override
    public int getCount() {
        return applications.size();
    }

    @Override
    public Object getItem(int position) {
        return applications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.app_list_item, parent, false);
        View appColor = rootView.findViewById(R.id.app_color);
        TextView appName = (TextView) rootView.findViewById(R.id.app_name);

        Application app = (Application) getItem(position);
        appColor.setBackgroundDrawable(context.getResources().getDrawable(app.getColor().getColorDrawableId()));
        appName.setText(app.getName());
        rootView.findViewById(R.id.go_to_app).setBackground(position != currentApp ? arrow : activeArrow);
        return rootView;
    }

    public void setCurrentApp(int position){
        currentApp = position;
    }
}
