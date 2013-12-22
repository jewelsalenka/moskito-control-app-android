package com.moskito;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.*;
import com.example.moskito_control_app_android.R;
import com.google.common.base.Objects;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.stub.entity.*;

import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class ApplicationActivity extends Activity {
    private static final String TAG = ApplicationActivity.class.getSimpleName();
    private static final String SHARED_PREFERENCES_KEY_URL = TAG + ".SharedPreferences.Url";
    private static final String SHARED_PREFERENCES_KEY_HTTP = TAG + ".SharedPreferences.Http";
    private static final String SHARED_PREFERENCES_KEY_PORT = TAG + ".SharedPreferences.Port";
    private static final String SHARED_PREFERENCES_KEY_LOGIN = TAG + ".SharedPreferences.Login";
    private static final String SHARED_PREFERENCES_KEY_INTERVAL = TAG + ".SharedPreferences.Interval";
    private static final String SHARED_PREFERENCES_KEY_PASS = TAG + ".SharedPreferences.Pass";
    private static final String SHARED_PREFERENCES_KEY_AUTH = TAG + ".SharedPreferences.Auth";
    private static final String MOSKITO_CONTROL_REST = "/moskito-control/rest/";
    private static final String HTTPS = "https://";
    private static final String HTTP = "http://";
    private static final int MILLISECONDS_IN_MINUTE = 60000;
    public static int INTERVAL_UPDATE_IN_MINUTES = 1;

    private final Helper mHelper = new Helper();
    private Application mCurrentApp;
    private TextView mNoDataView;
    private View mHeader;
    private TextView mAppTitleView;
    private TextView mMinutesUpdateInterval;
    private SlidingMenu mSliderMenu;
    private View mShowAppList;
    private View mShowSettings;
    private String mDefaultHttp = HTTP;
    private Handler mHandler;
    private Runnable mDataUpdater = new Runnable() {
        @Override
        public void run() {
            createConnection();
            mHandler.postDelayed(mDataUpdater, INTERVAL_UPDATE_IN_MINUTES * MILLISECONDS_IN_MINUTE);
        }
    };

    private void startDataUpdater(){
        mDataUpdater.run();
    }

    private void stopDataUpdater(){
        mHandler.removeCallbacks(mDataUpdater);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main);
        mHandler = new Handler();
        initSlidingMenu();
        obtainView();
        initHeader();
        initSettingsPanel();
        createConnection();
        updateHead();
    }

    private void initMultitouchPlot(String chd, String chxl, String chxr, String chds, String chco) {
        WebView webView = (WebView) findViewById(R.id.multitouchPlot);
        String content = String.format(GoogleChartAPI.CONTENT, chd, chxr, chds, chco, chxl);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.requestFocusFromTouch();
        webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }

    private void drawOnMultitouchPlot(int groupPosition) {
        if (mCurrentApp == null) return;
        Chart chart = mCurrentApp.getCharts().get(groupPosition);
        List<Line> lines = chart.getLines();
        //+"chxr=1,0,100&"
        StringBuilder chxr = new StringBuilder("1,");
        //+"chds=0,100&"
        StringBuilder chds = new StringBuilder("");
        //+"chd=t:7,18,11,26,22,11,14,7,18,11,26,22,11,14|26,22,11,14,7,11,26,22,11,14&"
        StringBuilder chd = new StringBuilder("t:");
        //+"chxl=0:|Mon|Tue|Wed|Thu|Fri|Sat|Sun&"
        StringBuilder chxl = new StringBuilder("0:");
        //+"chco=6EFE61,4d89f9,C030FF,FFFF72&"
        StringBuilder chco = new StringBuilder("");
        int index = 0;
        for (Point point : lines.get(0).getPoints()) {
            if (index%15 == 0){
                chxl.append("|");
                chxl.append(point.getxCaption());
            }
            index++;
        }
        index = 0;
        double max = 0;
        double min = 1000000;
        for (Line line : lines) {
            if (line.isDrawable()) {
                chco.append(line.getColor().getColor());
                chco.append(",");
                if (index != 0) chd.append("|");
                int pointsNum = 0;
                for (Point point : line.getPoints()) {
                    double y = point.getyValues();
                    max = Math.max(max, y);
                    min = Math.min(min, y);
                    chd.append(y);
                    pointsNum++;
                    if (pointsNum != line.getPoints().size()) chd.append(",");
                }
                index++;
            }
        }
        chxr.append(min < 100 ? 0 : min - 10);
        chxr.append(",");
        chxr.append(max);
        chds.append(min < 100 ? 0 : min - 10);
        chds.append(",");
        chds.append(max);
        chco.deleteCharAt(chco.length()-1);
        initMultitouchPlot(chd.toString(), chxl.toString(), chxr.toString(), chds.toString(), chco.toString());
    }

    private void initSlidingMenu() {
        mSliderMenu = new SlidingMenu(this);
        mSliderMenu.setMode(SlidingMenu.LEFT_RIGHT);
        mSliderMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSliderMenu.setShadowDrawable(R.drawable.shadow_left);
        mSliderMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSliderMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSliderMenu.setFadeDegree(0.35f);
        mSliderMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSliderMenu.setMenu(R.layout.applications);
        mSliderMenu.setSecondaryMenu(R.layout.settings);
        mSliderMenu.setSecondaryShadowDrawable(R.drawable.shadow_right);
    }

    private void initSettingsPanel() {
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        final EditText urlEditText = (EditText) findViewById(R.id.server_address_input);
        String url = preferences.getString(SHARED_PREFERENCES_KEY_URL, HTTP);
        urlEditText.setText(url);

        final ToggleButton http = (ToggleButton) findViewById(R.id.http_or_https);
        http.setChecked(preferences.getBoolean(SHARED_PREFERENCES_KEY_HTTP, true));
        http.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDefaultHttp = isChecked ? HTTP : HTTPS;
                String currentUrl = urlEditText.getText().toString();
                if (TextUtils.isEmpty(currentUrl)) urlEditText.setText(mDefaultHttp);
                else if (isChecked) urlEditText.setText(currentUrl.replaceFirst(HTTPS, HTTP));
                else urlEditText.setText(currentUrl.replaceFirst(HTTP, HTTPS));
            }
        });

        final EditText portEditText = (EditText) findViewById(R.id.server_port_input);
        portEditText.setText(preferences.getString(SHARED_PREFERENCES_KEY_PORT, ""));

        final CheckBox authorization = (CheckBox) findViewById(R.id.checkbox_authorization);
        boolean isAuthActive = preferences.getBoolean(SHARED_PREFERENCES_KEY_AUTH, false);
        authorization.setChecked(isAuthActive);
        int visibility = isAuthActive ? View.VISIBLE : View.GONE;
        final View loginView = findViewById(R.id.login);
        loginView.setVisibility(visibility);
        final View passwordView = findViewById(R.id.password);
        passwordView.setVisibility(visibility);
        final EditText loginTextView = (EditText) findViewById(R.id.login_input);
        String login = preferences.getString(SHARED_PREFERENCES_KEY_LOGIN, "");
        loginTextView.setText(login);
        final EditText passwordTextView = (EditText) findViewById(R.id.password_input);
        String pass = preferences.getString(SHARED_PREFERENCES_KEY_PASS, "");
        passwordTextView.setText(pass);
        final View passAndLogin = findViewById(R.id.textEditForLoginAndPass);
        passAndLogin.setVisibility(visibility);
        authorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = authorization.isChecked() ? View.VISIBLE : View.GONE;
                loginView.setVisibility(visibility);
                loginTextView.setText(preferences.getString(SHARED_PREFERENCES_KEY_LOGIN, ""));
                passwordView.setVisibility(visibility);
                passAndLogin.setVisibility(visibility);
            }
        });
        mMinutesUpdateInterval.setText(preferences.getInt(SHARED_PREFERENCES_KEY_INTERVAL, 1) + "");
        final Button minusMinutes = (Button) findViewById(R.id.minus_interval);
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

        final Button plusMinutes = (Button) findViewById(R.id.plus_interval);
        plusMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minutes = Integer.parseInt(mMinutesUpdateInterval.getText().toString());
                minutes++;
                mMinutesUpdateInterval.setText(String.valueOf(minutes));
            }
        });

        View saveBtn = findViewById(R.id.save_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDataUpdater();
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putString(SHARED_PREFERENCES_KEY_URL, TextUtils.isEmpty(urlEditText.getText()) ? mDefaultHttp
                        : urlEditText.getText().toString());
                editor.putString(SHARED_PREFERENCES_KEY_PORT, TextUtils.isEmpty(portEditText.getText()) ? "" :
                        portEditText.getText().toString());
                editor.putInt(SHARED_PREFERENCES_KEY_INTERVAL, TextUtils.isEmpty(mMinutesUpdateInterval.getText())
                        ? 1 : Integer.parseInt(mMinutesUpdateInterval.getText().toString()));
                editor.putString(SHARED_PREFERENCES_KEY_LOGIN, TextUtils.isEmpty(loginTextView.getText())
                        ? "" : loginTextView.getText().toString());
                editor.putString(SHARED_PREFERENCES_KEY_PASS, TextUtils.isEmpty(passwordTextView.getText())
                        ? "" : passwordTextView.getText().toString());
                editor.putBoolean(SHARED_PREFERENCES_KEY_HTTP, http.isChecked());
                editor.putBoolean(SHARED_PREFERENCES_KEY_AUTH, authorization.isChecked());
                editor.commit();
                INTERVAL_UPDATE_IN_MINUTES = getPreferences(MODE_PRIVATE).getInt(SHARED_PREFERENCES_KEY_INTERVAL, 1);
                mSliderMenu.toggle();
                createConnection();
                startDataUpdater();
                Toast toastNetwork = Toast.makeText(ApplicationActivity.this, R.string.save_settings, 1000);
                toastNetwork.show();
            }
        });
    }

    private void obtainView() {
        mNoDataView = (TextView) findViewById(R.id.textView_about_no_data_loading);
        mMinutesUpdateInterval = (TextView) findViewById(R.id.interval_minutes);
        final SlidingDrawer historyDrawer = (SlidingDrawer) findViewById(R.id.bottom_drawer);
        historyDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                historyDrawer.bringToFront();
                findViewById(R.id.history_arrow).setRotation(0);
            }
        });
        historyDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                findViewById(R.id.history_arrow).setRotation(180);
            }
        });

    }

    private void initHeader() {
        mHeader = findViewById(R.id.header);
        mAppTitleView = (TextView) mHeader.findViewById(R.id.application_title);
        mShowAppList = mHeader.findViewById(R.id.show_applications);
        mShowAppList.setOnClickListener(new View.OnClickListener() {
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
        View mHelp = mHeader.findViewById(R.id.help);
        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplicationActivity.this, HelpActivity.class);
                ApplicationActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.push_up_in_help, R.anim.static_out_help);
            }
        });
        final View refresh = mHeader.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = new RotateAnimation(0, 1080, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(2800);
                refresh.startAnimation(animation);
                createConnection();
            }
        });
        View graphics = mHeader.findViewById(R.id.chart);
        graphics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentApp == null) return;
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    if (mCurrentApp.getCharts().size() == 0) {
                        Toast toastNoCharts = Toast.makeText(ApplicationActivity.this, R.string.no_charts, 10000);
                        toastNoCharts.show();
                    } else {
                        SlidingDrawer chartSlidingDrawer = (SlidingDrawer) findViewById(R.id.charts_drawer);
                        if (chartSlidingDrawer.isOpened()) {
                            chartSlidingDrawer.animateClose();
                        } else {
                            chartSlidingDrawer.animateOpen();
                            drawOnMultitouchPlot(0);
                        }
                    }
                } else {
                    Toast toastNetwork = Toast.makeText(ApplicationActivity.this, R.string.no_network_connection, 20000);
                    toastNetwork.show();
                }
            }
        });
    }

    private void initializeComponentList() {
        final ComponentAdapter sAdapter = (mCurrentApp == null) ?
                new ComponentAdapter(this, null) : new ComponentAdapter(this, mCurrentApp.getComponents());
        final ExpandableListView componentList = (ExpandableListView) findViewById(R.id.list_of_components);
        componentList.setAdapter(sAdapter);
        setGroupIndicatorToRight(componentList);
        componentList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            View previousArrow;
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                View arrow = v.findViewById(R.id.show_hide_info);
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                    arrow.setRotation(0);
                } else {
                    parent.expandGroup(groupPosition);
                    if (previousArrow != null) previousArrow.setRotation(0);
                    arrow.setRotation(180);
                }
                previousArrow = arrow;
                return true;
            }
        });

        componentList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup) {
                    componentList.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });
    }

    private void initializeAppsList(List<Application> apps) {
        final AppAdapter adapter = new AppAdapter(this, apps);
        ListView applicationList = (ListView) findViewById(R.id.app_list);
        applicationList.setAdapter(adapter);
        applicationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SlidingDrawer chartSlidingDrawer = (SlidingDrawer) findViewById(R.id.charts_drawer);
                if (chartSlidingDrawer.isOpened()) {
                    chartSlidingDrawer.animateClose();
                }
                mSliderMenu.toggle();
                updateAppData(adapter.getItem(position));
                adapter.setCurrentApp(position);
                adapter.notifyDataSetChanged();
                if (mCurrentApp.getHistory().isEmpty()) {
                    new HistoryGetter().execute(getAddressConnection() + "history");
                } else {
                    initializeHistoryList();
                }
                if (mCurrentApp.getCharts().isEmpty()) {
                    new ChartsGetter().execute(getAddressConnection() + "charts/points");
                } else {
                    initializeChartsList();
                }
            }
        });
    }

    private void initializeHistoryList() {
        final HistoryAdapter historyAdapter = (mCurrentApp == null) ?
                new HistoryAdapter(this, null) :
                new HistoryAdapter(this, mCurrentApp.getHistory());
        final ExpandableListView historyList = (ExpandableListView) findViewById(R.id.history_list);
        setGroupIndicatorToRight(historyList);
        historyList.setAdapter(historyAdapter);
        historyList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                } else {
                    parent.expandGroup(groupPosition);
                }
                return true;
            }

        });
        historyList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup) {
                    historyList.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });
    }

    private void initializeChartsList() {
        if (mCurrentApp == null) return;
        final ChartsAdapter chartsAdapter = new ChartsAdapter(mCurrentApp.getCharts());
        final ExpandableListView chartListView = (ExpandableListView) findViewById(R.id.charts_list);
        chartListView.setAdapter(chartsAdapter);
        setGroupIndicatorToRight(chartListView);
        chartListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Chart chart = chartsAdapter.getGroup(groupPosition);
                Line line = chartsAdapter.getChild(groupPosition, childPosition);
                if (chart.getNumOfLinesWhichIsDrawable() != 1 || (!line.isDrawable())){
                    line.setDrawable(!line.isDrawable());
                    chartsAdapter.notifyDataSetChanged();
                    drawOnMultitouchPlot(groupPosition);
                }
                return true;
            }
        });
        chartListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            View previousArrow;
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                View arrow = v.findViewById(R.id.charts_group_indicator);
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                    arrow.setRotation(0);
                } else {
                    parent.expandGroup(groupPosition);
                    if (previousArrow != null) previousArrow.setRotation(0);
                    arrow.setRotation(180);
                }
                previousArrow = arrow;
                drawOnMultitouchPlot(groupPosition);

                return true;
            }

        });
        chartListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup) {
                    chartListView.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });
    }

    private void setGroupIndicatorToRight(ExpandableListView expListView) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        expListView.setIndicatorBounds(width - getDipsFromPixel(5), width - getDipsFromPixel(5));
    }

    public int getDipsFromPixel(float pixels) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }

    private void updateAppData(Application app) {
        mCurrentApp = app;
        updateData();
    }

    private void updateData() {
        updateHead();
        initializeComponentList();
        initializeHistoryList();
        updateNoData();
    }

    private void updateHead() {
        if (getPreferences(MODE_PRIVATE).getString(SHARED_PREFERENCES_KEY_URL, HTTP).equals(HTTP)) return;
        if (mCurrentApp == null) {
            mHeader.setBackgroundDrawable(getResources().getDrawable(R.color.light_grey));
            mAppTitleView.setText("App");
        } else {
            mHeader.setBackgroundDrawable(getResources().getDrawable(mCurrentApp.getColor().getColorId()));
            mAppTitleView.setText(mCurrentApp.getName());
        }
    }

    private void updateNoData() {
        if ((mCurrentApp == null) || (mCurrentApp.getComponents() == null) || (mCurrentApp.getComponents().size() == 0)) {
            mNoDataView.setVisibility(View.VISIBLE);
        } else {
            mNoDataView.setVisibility(View.GONE);
        }
    }

    public void createConnection() {
        String stringUrl = getAddressConnection() + "control";
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (((CheckBox)findViewById(R.id.checkbox_authorization)).isChecked()){
                final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                String login = preferences.getString(SHARED_PREFERENCES_KEY_LOGIN, "");
                String pass = preferences.getString(SHARED_PREFERENCES_KEY_PASS, "");
                mHelper.setHttpAuth(login, pass);
            }
            new ApplicationGetter().execute(stringUrl);
        } else {
            Toast toastNetwork = Toast.makeText(ApplicationActivity.this, R.string.no_network_connection, 20000);
            toastNetwork.show();

        }
    }

    private String getAddressConnection() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String url = preferences.getString(SHARED_PREFERENCES_KEY_URL, HTTP);
        helpInfo(url.equals(HTTP));
        String port = preferences.getString(SHARED_PREFERENCES_KEY_PORT, "");
        return url + ":" + port + MOSKITO_CONTROL_REST;
    }

    private class ApplicationGetter extends AsyncTask<String, Void, List<Application>> {
        @Override
        protected List<Application> doInBackground(String... urls) {
            try {
                return mHelper.getAllApps(urls[0]);
            } catch (RuntimeException e) {
                Log.w(TAG, "failed to get list of all apps. ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Application> apps) {
            if (apps != null) {
                Application current = null;
                if (!apps.isEmpty()){
                    int index = 0;
                    if (mCurrentApp != null){
                        int i = 0;
                        String currentAppName = mCurrentApp.getName();
                        for (Application app : apps){
                            if (app.getName().equals(currentAppName)){
                                index = i;
                                break;
                            }
                            i++;
                        }
                    }
                    current = apps.get(index);
                }
                updateAppData(current);
                if (apps.isEmpty()) return;
                initializeAppsList(apps);
                new HistoryGetter().execute(getAddressConnection() + "history");
                new ChartsGetter().execute(getAddressConnection() + "charts/points");
            } else {
                mNoDataView.setText("Connection failed. URL may be invalid.");
                updateNoData();
            }
        }
    }

    private class HistoryGetter extends AsyncTask<String, Void, List<HistoryItem>> {
        private final Application mCurrentHistoryApp = mCurrentApp;

        @Override
        protected List<HistoryItem> doInBackground(String... urls) {
            try {
                return mHelper.getHistory(urls[0], mCurrentHistoryApp.getName());
            } catch (RuntimeException e) {
                Log.w(TAG, "Failed to get list of history. ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<HistoryItem> history) {
            if (history != null) {
                mCurrentHistoryApp.setHistory(history);
                initializeHistoryList();
            } else {
                mNoDataView.setText("Connection failed while trying to get history. URL may be invalid.");
                updateNoData();
            }
        }
    }

    private class ChartsGetter extends AsyncTask<String, Void, List<Chart>> {
        private final Application mChartsApp = mCurrentApp;
        @Override
        protected List<Chart> doInBackground(String... urls) {
            try {
                return mHelper.getCharts(urls[0], mChartsApp.getName());
            } catch (RuntimeException e) {
                Log.v(TAG, "Failed to get charts. ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Chart> charts) {
            if (charts != null) {
                mChartsApp.addCharts(charts);
                initializeChartsList();
            } else {
                mNoDataView.setText("Connection failed while trying to get charts. URL may be invalid.");
                updateNoData();
            }
        }
    }

    public void helpInfo(boolean visible) {
        View help1 = findViewById(R.id.image_help1);
        View help2 = findViewById(R.id.image_help2);
        int visibility = visible ? View.VISIBLE : View.GONE;
        help1.setVisibility(visibility);
        help2.setVisibility(visibility);
        if (visible) mHeader.setBackgroundDrawable(getResources().getDrawable(R.color.blue));
    }
}
