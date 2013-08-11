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
import com.stub.entity.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 14.07.13
 */
public class ComponentAdapter extends BaseAdapter{
    private List<Component> components;
    private final Context context;

    public ComponentAdapter(Context context, List<Component> components) {
        this.context = context;
        if (components != null){
            this.components = components;
        } else {
            this.components = new ArrayList<Component>();
        }
    }
    @Override
    public int getCount() {
        return components.size();
    }

    @Override
    public Object getItem(int position) {
        return components.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = convertView;
        if (rootView == null){
            rootView = LayoutInflater.from(context).inflate(R.layout.server_list_item_advanced, parent, false);
        }
        View componentStateView = rootView.findViewById(R.id.server_color_state);
        TextView appName = (TextView) rootView.findViewById(R.id.server_name);

        Component component = (Component) getItem(position);
        Drawable color = context.getResources().getDrawable(component.getState().getColorId());
        componentStateView.setBackgroundDrawable(color);
        appName.setText(component.getName());

        RelativeLayout advancedLayout = (RelativeLayout) rootView.findViewById(R.id.item_advanced);

        if (rootView.isSelected()){
            advancedLayout.setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.info_text)).setText(component.getInfo());
            ((TextView) rootView.findViewById(R.id.info_date)).setText(component.getDate().toLocaleString());
        } else {
            advancedLayout.setVisibility(View.GONE);
        }
        return rootView;
    }
}
