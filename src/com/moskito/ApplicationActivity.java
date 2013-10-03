package com.moskito;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.moskito_control_app_android.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.stub.entity.*;

import java.io.IOException;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class ApplicationActivity extends Activity{
    private static Helper mHelper;
    private Application currentApp;
    private TextView noDataView;
    private View header;
    TextView appTitleView;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.main);
        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
//        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.applications);
        obtainView();
        createConnection();
    }

    private void obtainView(){
        noDataView = (TextView) findViewById(R.id.no_data);
        header = findViewById(R.id.header);
        appTitleView = (TextView) header.findViewById(R.id.application_title);
        updateHead();

    }

    private void initializeServersList(){
        final ComponentAdapter sAdapter = new ComponentAdapter(this, currentApp.getComponents());
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
                new HistoryGetter().execute("http://server04.test.anotheria.net:8999/moskito-control/rest/history");
            }
        });
    }

    private void initializeHistoryList(){
        final HistoryAdapter historyAdapter = new HistoryAdapter(this, currentApp.getHistory());
        ListView lvSimple = (ListView) findViewById(R.id.history_list);
        lvSimple.setAdapter(historyAdapter);
        lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout advancedLayout = (RelativeLayout) view.findViewById(R.id.item_history_advanced);
                view.setSelected(!(advancedLayout.getVisibility() == View.VISIBLE));
                historyAdapter.notifyDataSetChanged();
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

        if (currentApp == null){
            header.setBackgroundDrawable(getResources().getDrawable(R.color.light_grey));
            appTitleView.setText("App");
        } else {
            header.setBackgroundDrawable(getResources().getDrawable(currentApp.getColor().getColorId()));
            appTitleView.setText(currentApp.getName());
        }
    }

    private void updateNoData(){
        if ((currentApp.getComponents() == null) || (currentApp.getComponents().size() == 0)){
            noDataView.setVisibility(View.VISIBLE);
        } else {
            noDataView.setVisibility(View.GONE);
        }
    }

    public void createConnection() {
        String stringUrl = "http://server04.test.anotheria.net:8999/moskito-control/rest/control";
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new HelperConnection().execute(stringUrl);
        } else {
            noDataView.setText("No network connection available.");
        }
    }

    private class HelperConnection extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                mHelper = new Helper(urls[0]);
                return "Connection established";
            } catch (IOException e) {
                noDataView.setText("Connection failed. URL may be invalid.");
                return "Connection failed. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            initializeAppsList();
            updateAppData(mHelper.getAllApps().get(0));
            new HistoryGetter().execute("http://server04.test.anotheria.net:8999/moskito-control/rest/history");
        }
    }

    private class HistoryGetter extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                currentApp.addHistory(mHelper.getHistory(urls[0], currentApp.getName()));
                return "Connection established";
            } catch (IOException e) {
                noDataView.setText("Connection failed. URL may be invalid.");
                return "Connection failed. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            initializeHistoryList();
            //updateAppData(mHelper.getAllApps().get(0));
        }
    }
}
