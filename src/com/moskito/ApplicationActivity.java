package com.moskito;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.moskito_control_app_android.R;
import com.stub.entity.Application;
import com.stub.entity.Server;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class ApplicationActivity extends Activity{
    private Application currentApp;
    private TextView noDataView;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.application);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            currentApp = (Application) extras.get(ApplicationsListActivity.KEY_APP);
        }
        obtainView();
        initializeList();
        updateNoData();
    }

    private void obtainView(){
        noDataView = (TextView) findViewById(R.id.no_data);
        View header = findViewById(R.id.header);
        header.setBackground(getResources().getDrawable(currentApp.getColor().getColorId()));
        View showApplicationsView = findViewById(R.id.show_applications);
        showApplicationsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplicationActivity.this, ApplicationsListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ApplicationsListActivity.KEY_APP, currentApp);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        TextView history = (TextView) findViewById(R.id.history_button);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplicationActivity.this, HistoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ApplicationsListActivity.KEY_APP, currentApp);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initializeList(){
        ServerAdapter sAdapter = new ServerAdapter(this, currentApp.getServers());
        ListView lvSimple = (ListView) findViewById(R.id.servers_list);
        lvSimple.setAdapter(sAdapter);
        lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                view = findViewById(R.id.server_list_item_advanced);


            }
        });

    }

    private void updateNoData(){
        if ((currentApp.getServers() == null) || (currentApp.getServers().size() == 0)){
            noDataView.setVisibility(View.VISIBLE);
        } else {
            noDataView.setVisibility(View.GONE);
        }
    }
}
