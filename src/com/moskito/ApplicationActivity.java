package com.moskito;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.moskito_control_app_android.R;
import com.stub.entity.*;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class ApplicationActivity extends Activity{
    private static Helper mHelper;
    private Application currentApp;
    private TextView noDataView;
    private SlidingDrawer leftDrawer;
    private View header;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.main);
        if (mHelper == null){
            mHelper = new Helper();
        }
        currentApp = mHelper.getAllApps().get(0);
        obtainView();
        initializeServersList();
        initializeAppsList();
        initializeHistoryList();
        updateNoData();
    }

    private void obtainView(){
        leftDrawer = (SlidingDrawer) findViewById(R.id.left_drawer);
        noDataView = (TextView) findViewById(R.id.no_data);
        header = findViewById(R.id.header);
        updateHead();
    }

    private void initializeServersList(){
        final ServerAdapter sAdapter = new ServerAdapter(this, currentApp.getServers());
        ListView lvSimple = (ListView) findViewById(R.id.servers_list);
        lvSimple.setAdapter(sAdapter);
        lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout advancedLayout = (RelativeLayout) view.findViewById(R.id.item_advanced);
                view.setSelected(!(advancedLayout.getVisibility() == View.VISIBLE));
                sAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initializeAppsList(){
        AppAdapter sAdapter = new AppAdapter(this, mHelper.getAllApps());
        ListView lvSimple = (ListView) findViewById(R.id.app_list);
        lvSimple.setAdapter(sAdapter);
        lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateAppData(mHelper.getAllApps().get(position));
                leftDrawer.close();
            }
        });
    }

    private void initializeHistoryList(){
        HistoryAdapter historyAdapter = new HistoryAdapter(this, currentApp.getHistory());
        ListView lvSimple = (ListView) findViewById(R.id.history_list);
        lvSimple.setAdapter(historyAdapter);
        lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
    }

    private void updateAppData(Application app){
        currentApp = app;
        updateHead();
        initializeServersList();
        initializeHistoryList();
        updateNoData();
    }

    private void updateHead(){
        header.setBackground(getResources().getDrawable(currentApp.getColor().getColorId()));
    }

    private void updateNoData(){
        if ((currentApp.getServers() == null) || (currentApp.getServers().size() == 0)){
            noDataView.setVisibility(View.VISIBLE);
        } else {
            noDataView.setVisibility(View.GONE);
        }
    }
}
