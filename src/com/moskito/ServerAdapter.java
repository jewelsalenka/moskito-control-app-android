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
import com.stub.entity.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 14.07.13
 */
public class ServerAdapter extends BaseAdapter{
    private List<Server> servers;
    private final Context context;

    public ServerAdapter(Context context, List<Server> servers) {
        this.context = context;
        if (servers != null){
            this.servers = servers;
        } else {
            this.servers = new ArrayList<Server>();
        }
    }
    @Override
    public int getCount() {
        return servers.size();
    }

    @Override
    public Object getItem(int position) {
        return servers.get(position);
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
        View serverColor = rootView.findViewById(R.id.server_color_state);
        TextView appName = (TextView) rootView.findViewById(R.id.server_name);

        Server server = (Server) getItem(position);
        Drawable color = context.getResources().getDrawable(server.getState().getColorId());
        serverColor.setBackgroundDrawable(color);
        appName.setText(server.getName());

        RelativeLayout advancedLayout = (RelativeLayout) rootView.findViewById(R.id.item_advanced);

        if (rootView.isSelected()){
            advancedLayout.setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.info_text)).setText(server.getInfo());
            ((TextView) rootView.findViewById(R.id.info_date)).setText(server.getDate().toLocaleString());
        } else {
            advancedLayout.setVisibility(View.GONE);
        }
        return rootView;
    }
}
