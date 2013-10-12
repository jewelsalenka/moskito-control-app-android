package com.moskito;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
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
    private Application mCurrentApp;
    private TextView mNoDataView;
    private View mHeader;
    private TextView mAppTitleView;
    private EditText mLogin;
    private EditText mPassword;
    private TextView mMinutesUpdateInterval;
    private SlidingMenu mSliderMenu;
    private View mShowAppList;
    private View mShowSettings;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.main);
        obtainView();
        initHeader();
        initSlidingMenu();
        initSettingsPanel();
        createConnection();
        updateHead();
    }

    private void initSlidingMenu(){
        mSliderMenu = new SlidingMenu(this);
        mSliderMenu.setMode(SlidingMenu.LEFT_RIGHT);
        mSliderMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSliderMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSliderMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSliderMenu.setFadeDegree(0.35f);
        mSliderMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSliderMenu.setMenu(R.layout.applications);
        mSliderMenu.setSecondaryMenu(R.layout.settings);
        mSliderMenu.setSecondaryShadowDrawable(R.drawable.right_shadow);
    }

    private void initSettingsPanel(){
        final CheckBox authorization = (CheckBox) findViewById(R.id.checkbox_authorization);
        final View login = findViewById(R.id.login);
        final View password = findViewById(R.id.password);
        final View intervalText = findViewById(R.id.update_interval_text);
        final RelativeLayout interval = (RelativeLayout) findViewById(R.id.update_interval);
        final View save = findViewById(R.id.save_button);
        authorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authorization.isChecked()){
                    login.setVisibility(View.VISIBLE);
                    mLogin.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    mPassword.setVisibility(View.VISIBLE);
                    interval.setVisibility(View.VISIBLE);
                    save.setVisibility(View.VISIBLE);
                    intervalText.setVisibility(View.VISIBLE);
                } else {
                    login.setVisibility(View.INVISIBLE);
                    mLogin.setVisibility(View.INVISIBLE);
                    password.setVisibility(View.INVISIBLE);
                    mPassword.setVisibility(View.INVISIBLE);
                    interval.setVisibility(View.INVISIBLE);
                    save.setVisibility(View.INVISIBLE);
                    intervalText.setVisibility(View.INVISIBLE);
                }
            }
        });

        Button minusMinutes = (Button) interval.findViewById(R.id.minus_interval);
        minusMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minutes = Integer.parseInt(mMinutesUpdateInterval.getText().toString());
                if (minutes > 1) {
                    minutes--;
                    mMinutesUpdateInterval.setText(String.valueOf(minutes));
                }
            }
        });

        Button plusMinutes = (Button) interval.findViewById(R.id.plus_interval);
        plusMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minutes = Integer.parseInt(mMinutesUpdateInterval.getText().toString());
                minutes++;
                mMinutesUpdateInterval.setText(String.valueOf(minutes));
            }
        });
    }
    private void obtainView(){
        mNoDataView = (TextView) findViewById(R.id.no_data);
        mLogin = (EditText) findViewById(R.id.login_input);
        mPassword = (EditText) findViewById(R.id.password_input);
        mMinutesUpdateInterval = (TextView) findViewById(R.id.interval_minutes);

    }
    private void initHeader(){
        mHeader = findViewById(R.id.header);
        mAppTitleView = (TextView) mHeader.findViewById(R.id.application_title);
        mShowAppList = mHeader.findViewById(R.id.show_applications);
        mShowAppList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mSliderMenu.toggle();
            }
        });
        mShowSettings = mHeader.findViewById(R.id.settings);
        mShowSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSliderMenu.showSecondaryMenu();
            }
        });
    }
    private void initializeServersList(){
        final ComponentAdapter sAdapter = new ComponentAdapter(this, mCurrentApp.getComponents());
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
                mSliderMenu.toggle();
                updateAppData(mHelper.getAllApps().get(position));
                new HistoryGetter().execute("http://server04.test.anotheria.net:8999/moskito-control/rest/history");
            }
        });
    }

    private void initializeHistoryList(){
        final HistoryAdapter historyAdapter = new HistoryAdapter(this, mCurrentApp.getHistory());
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
        mCurrentApp = app;
        updateHead();
        initializeServersList();
        initializeHistoryList();
        updateNoData();
    }

    private void updateHead(){

        if (mCurrentApp == null){
            mHeader.setBackgroundDrawable(getResources().getDrawable(R.color.light_grey));
            mAppTitleView.setText("App");
        } else {
            mHeader.setBackgroundDrawable(getResources().getDrawable(mCurrentApp.getColor().getColorId()));
            mAppTitleView.setText(mCurrentApp.getName());
        }
    }

    private void updateNoData(){
        if ((mCurrentApp.getComponents() == null) || (mCurrentApp.getComponents().size() == 0)){
            mNoDataView.setVisibility(View.VISIBLE);
        } else {
            mNoDataView.setVisibility(View.GONE);
        }
    }

    public void createConnection() {
        String stringUrl = "http://server04.test.anotheria.net:8999/moskito-control/rest/control";
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new HelperConnection().execute(stringUrl);
        } else {
            mNoDataView.setText("No network connection available.");
        }
    }

    private class HelperConnection extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                mHelper = new Helper(urls[0]);
                return "Connection established";
            } catch (IOException e) {
                mNoDataView.setText("Connection failed. URL may be invalid.");
                return "Connection failed. URL may be invalid.";
            }
        }
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
                mCurrentApp.addHistory(mHelper.getHistory(urls[0], mCurrentApp.getName()));
                return "Connection established";
            } catch (IOException e) {
                mNoDataView.setText("Connection failed. URL may be invalid.");
                return "Connection failed. URL may be invalid.";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            initializeHistoryList();
        }
    }
}
