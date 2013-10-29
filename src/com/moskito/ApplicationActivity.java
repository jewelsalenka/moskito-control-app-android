package com.moskito;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.*;
import com.androidplot.xy.*;
import com.example.moskito_control_app_android.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.stub.entity.*;

import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class ApplicationActivity extends Activity {
    public static final String APP_KEY = ApplicationActivity.class.getSimpleName() + ".CurrentApp";
    private static final String SHARED_PREFERENCES_KEY_URL = ApplicationActivity.class.getSimpleName()
            + ".SharedPreferences.Url";
    private static final String SHARED_PREFERENCES_KEY_HTTP = ApplicationActivity.class.getSimpleName()
            + ".SharedPreferences.Http";
    private static final String SHARED_PREFERENCES_KEY_PORT = ApplicationActivity.class.getSimpleName()
            + ".SharedPreferences.Port";
    private static final String SHARED_PREFERENCES_KEY_LOGIN = ApplicationActivity.class.getSimpleName()
            + ".SharedPreferences.Login";
    private static final String SHARED_PREFERENCES_KEY_INTERVAL = ApplicationActivity.class.getSimpleName()
            + ".SharedPreferences.Interval";
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    private static final String MOSKITO_CONTROL_REST = "/moskito-control/rest/";

    private static Helper mHelper;
    private Application mCurrentApp;
    private TextView mNoDataView;
    private View mHeader;
    private TextView mAppTitleView;
    private TextView mMinutesUpdateInterval;
    private SlidingMenu mSliderMenu;
    private View mShowAppList;
    private View mShowSettings;
    private String mDefaultHttp = HTTP;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main);
        initSlidingMenu();
        obtainView();
        initHeader();
        initSettingsPanel();
        createConnection();
        updateHead();
    }

    private void initMultitouchPlot(String chd, String chxl, String chxr, String chds) {
        WebView webView = (WebView) findViewById(R.id.multitouchPlot);
        String CHART_URL =  "http://chart.apis.google.com/chart?"
                +"cht=lc&"
                +"chs=330x200&"
                + chd
                + chxr
                + chds
                +"chco=6EFE61,4d89f9,C030FF,FFFF72&"
                +"chxt=x,y&"
                + chxl
                +"chls=3,1,0|3,1,0&"
                +"chg=0,6.67,5,5";
        webView.loadUrl(CHART_URL);
    }

    private void drawOnMultitouchPlot(int groupPosition){
        Chart chart = mCurrentApp.getCharts().get(groupPosition);
        List<Line> lines = chart.getLines();
        //+"chxr=1,0,100&"
        StringBuilder chxr = new StringBuilder();
        chxr.append("chxr=1,0,");
        //+"chds=0,100&"
        StringBuilder chds = new StringBuilder();
        chds.append("chds=0,");
        //+"chd=t:7,18,11,26,22,11,14,7,18,11,26,22,11,14|26,22,11,14,7,11,26,22,11,14&"
        StringBuilder chd = new StringBuilder();
        chd.append("chd=t:");
        //+"chxl=0:|Mon|Tue|Wed|Thu|Fri|Sat|Sun&"
        StringBuilder chxl = new StringBuilder();
        chxl.append("chxl=0:");
        int index = 0;
        for (Point point : lines.get(0).getPoints()) {
            if (index%10 == 0){
                chxl.append("|");
                chxl.append(point.getxCaption());
            }
            index++;
        }
        chxl.append("&");
        index = 0;
        double max = 0;
        for (Line line : lines) {
            if (line.isDrawable()) {
                if (index != 0) chd.append("|");
                int pointsNum = 0;
                for (Point point : line.getPoints()) {
                    double y = point.getyValues();
                    max = Math.max(max, y);
                    chd.append(y);
                    pointsNum++;
                    if (!(pointsNum == line.getPoints().size())) chd.append(",");
                }
                index++;
            }
        }
        chd.append("&");
        chxr.append(max);
        chxr.append("&");
        chds.append(max);
        chds.append("&");
        Log.i("AndroidRuntime", "chd" + chd.toString());
        Log.i("AndroidRuntime", "chxl" + chxl.toString());
        initMultitouchPlot(chd.toString(), chxl.toString(), chxr.toString(), chds.toString());
    }

    private void initSlidingMenu() {
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

    private void initSettingsPanel() {
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        ToggleButton http = (ToggleButton) findViewById(R.id.http_or_https);
        http.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDefaultHttp = isChecked ? HTTP : HTTPS;
            }
        });

        final EditText urlEditText = (EditText) findViewById(R.id.server_address_input);
        String url = preferences.getString(SHARED_PREFERENCES_KEY_URL, HTTP);
        urlEditText.setText(url);

        final EditText portEditText = (EditText) findViewById(R.id.server_port_input);
        portEditText.setText(preferences.getString(SHARED_PREFERENCES_KEY_PORT, ""));

        final CheckBox authorization = (CheckBox) findViewById(R.id.checkbox_authorization);
        final View loginView = findViewById(R.id.login);
        final View passwordView = findViewById(R.id.password);
        final EditText loginTextView = (EditText) findViewById(R.id.login_input);
        String login = preferences.getString(SHARED_PREFERENCES_KEY_LOGIN, "");
        loginTextView.setText(login);
        final EditText passwordTextView = (EditText) findViewById(R.id.password_input);
        authorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = authorization.isChecked() ? View.VISIBLE : View.GONE;
                loginView.setVisibility(visibility);
                loginTextView.setVisibility(visibility);
                passwordView.setVisibility(visibility);
                passwordTextView.setVisibility(visibility);
            }
        });
        mMinutesUpdateInterval.setText(preferences.getString(SHARED_PREFERENCES_KEY_INTERVAL, "1"));
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
                Log.i("AndroidRuntime", "Yahoooo");
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putString(SHARED_PREFERENCES_KEY_URL, TextUtils.isEmpty(urlEditText.getText()) ? mDefaultHttp
                        : urlEditText.getText().toString());
                editor.putString(SHARED_PREFERENCES_KEY_PORT, TextUtils.isEmpty(portEditText.getText()) ? "" :
                        portEditText.getText().toString());
                editor.putString(SHARED_PREFERENCES_KEY_INTERVAL, TextUtils.isEmpty(mMinutesUpdateInterval.getText())
                        ? "1" : mMinutesUpdateInterval.getText().toString());
                editor.putString(SHARED_PREFERENCES_KEY_LOGIN, TextUtils.isEmpty(loginTextView.getText())
                        ? "" : loginTextView.getText().toString());
                editor.commit();
                createConnection();
            }
        });
    }

    private void obtainView() {
        mNoDataView = (TextView) findViewById(R.id.no_data);
        mMinutesUpdateInterval = (TextView) findViewById(R.id.interval_minutes);
        final SlidingDrawer historyDrawer = (SlidingDrawer) findViewById(R.id.bottom_drawer);
        historyDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                historyDrawer.bringToFront();
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
            }
        });
        View graphics = mHeader.findViewById(R.id.chart);
        graphics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentApp.getCharts().size() == 0) {
                    View popUpView = getLayoutInflater().inflate(R.layout.no_charts, null);
                    final PopupWindow mpopup = new PopupWindow(popUpView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true); //Creation of popup
                    mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
                    mpopup.showAtLocation(popUpView, Gravity.CENTER, 0, 0);
                    popUpView.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mpopup.dismiss();
                        }
                    });
                } else {
                    SlidingDrawer chartSlidingDrawer = (SlidingDrawer) findViewById(R.id.charts_drawer);
                    if (chartSlidingDrawer.isOpened()) {
                        chartSlidingDrawer.animateClose();
                    } else {
                        chartSlidingDrawer.animateOpen();
                        drawOnMultitouchPlot(0);
                    }

                }
            }
        });
    }

    private double findMaxValue(List<Double> y) {
        double max = 0;
        for (Double n : y) {
            max = Math.max(max, n);
        }
        return max;
    }

    private class GraphXLabelFormat extends Format {

        private String[] mLabels;

        private GraphXLabelFormat(List<String> labels) {
            this.mLabels = labels.toArray(new String[labels.size()]);
        }

        @Override
        public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
            int parsedInt = Math.round(Float.parseFloat(object.toString()));
            String labelString = mLabels[parsedInt];
            buffer.append(labelString);
            return buffer;
        }

        @Override
        public Object parseObject(String string, ParsePosition position) {
            return java.util.Arrays.asList(mLabels).indexOf(string);
        }
    }

    private void initializeServersList() {
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

    private void initializeAppsList() {
        final AppAdapter sAdapter = new AppAdapter(this, mHelper.getAllApps());
        ListView lvSimple = (ListView) findViewById(R.id.app_list);
        lvSimple.setAdapter(sAdapter);
        lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSliderMenu.toggle();
                updateAppData(mHelper.getAllApps().get(position));
                sAdapter.setCurrentApp(position);
                sAdapter.notifyDataSetChanged();
                new HistoryGetter().execute(getAddressConnection() + "history");
                new ChartsGetter().execute(getAddressConnection() + "charts/points");
            }
        });
    }

    private void initializeHistoryList() {
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

    private void initializeChartsList() {
        final ChartsAdapter chartsAdapter = new ChartsAdapter(this, mCurrentApp.getCharts());
        ExpandableListView chartListView = (ExpandableListView) findViewById(R.id.charts_list);
        setGroupIndicatorToRight(chartListView);
        chartListView.setAdapter(chartsAdapter);
        chartListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Line line = mCurrentApp.getCharts().get(groupPosition).getLines().get(childPosition);
                line.setDrawable(!line.isDrawable());
                chartsAdapter.notifyDataSetChanged();
                drawOnMultitouchPlot(groupPosition);
                return true;
            }
        });
        chartListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                } else {
                    parent.expandGroup(groupPosition);
                }
                drawOnMultitouchPlot(groupPosition);
                return true;
            }
        });
    }

    private void setGroupIndicatorToRight(ExpandableListView expListView) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width - getDipsFromPixel(5));
    }

    public int getDipsFromPixel(float pixels) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }

    private void updateAppData(Application app) {
        mCurrentApp = app;
        updateHead();
        initializeServersList();
        initializeHistoryList();
        updateNoData();
    }

    private void updateHead() {

        if (mCurrentApp == null) {
            mHeader.setBackgroundDrawable(getResources().getDrawable(R.color.light_grey));
            mAppTitleView.setText("App");
        } else {
            mHeader.setBackgroundDrawable(getResources().getDrawable(mCurrentApp.getColor().getColorId()));
            mAppTitleView.setText(mCurrentApp.getName());
        }
    }

    private void updateNoData() {
        if ((mCurrentApp.getComponents() == null) || (mCurrentApp.getComponents().size() == 0)) {
            mNoDataView.setVisibility(View.VISIBLE);
        } else {
            mNoDataView.setVisibility(View.GONE);
        }
    }

    public void createConnection() {
        String stringUrl = getAddressConnection() + "control";
        Log.i("AndroidRuntime", stringUrl);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new HelperConnection().execute(stringUrl);
        } else {
            mNoDataView.setText("No network connection available.");
        }
    }

    private String getAddressConnection() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String url = preferences.getString(SHARED_PREFERENCES_KEY_URL, HTTP);
        String port = preferences.getString(SHARED_PREFERENCES_KEY_PORT, "");
        return url + ":" + port + MOSKITO_CONTROL_REST;
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
            if (mHelper.getAllApps().isEmpty()) return;
            updateAppData(mHelper.getAllApps().get(0));
            new HistoryGetter().execute(getAddressConnection() + "history");
            new ChartsGetter().execute(getAddressConnection() + "charts/points");
        }
    }

    private class HistoryGetter extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                mCurrentApp.addHistory(mHelper.getHistory(urls[0], mCurrentApp.getName()));
                return "Connection established";
            } catch (IOException e) {
                mNoDataView.setText("Connection failed while trying to get history. URL may be invalid.");
                return "Connection failed while trying to get history. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            initializeHistoryList();
        }
    }

    private class ChartsGetter extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                mCurrentApp.addCharts(mHelper.getCharts(urls[0], mCurrentApp.getName()));
                return "Connection established";
            } catch (IOException e) {
                mNoDataView.setText("Connection failed while trying to get charts. URL may be invalid.");
                return "Connection failed while trying to get charts. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            initializeChartsList();
        }
    }
}
