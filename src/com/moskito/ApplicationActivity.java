package com.moskito;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class ApplicationActivity extends Activity{
    public static final String APP_KEY = ApplicationActivity.class.getSimpleName()+".CurrentApp";
    private static Helper mHelper;
    private Application mCurrentApp;
    private TextView mNoDataView;
    private View mHeader;
    private TextView mAppTitleView;
    private TextView mMinutesUpdateInterval;
    private SlidingMenu mSliderMenu;
    private View mShowAppList;
    private View mShowSettings;
    private  MultitouchPlot mMultitouchPlot;


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

    private void initMultitouchPlot() {
        if (mCurrentApp.getCharts().size() == 0) return;
        SlidingDrawer chartSlidingDrawer = (SlidingDrawer) findViewById(R.id.charts_drawer);
        mMultitouchPlot = (MultitouchPlot) chartSlidingDrawer.findViewById(R.id.multitouchPlot);
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(255, 255, 236));
        backgroundPaint.setStyle(Paint.Style.FILL);
        mMultitouchPlot.getGraphWidget().setBackgroundPaint(backgroundPaint);
        mMultitouchPlot.getLegendWidget().setVisible(false);
        mMultitouchPlot.getGraphWidget().setGridBackgroundPaint(backgroundPaint);
        // Reduce the number of range labels
        mMultitouchPlot.setTicksPerRangeLabel(3);
        // By default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        // multitouchPlot.disableAllMarkup();
        drawOnMultitouchPlot(0);
    }

    private void drawOnMultitouchPlot(int groupPosition){
        mMultitouchPlot.clear();
        Chart chart = mCurrentApp.getCharts().get(groupPosition);
        List<Line> lines = chart.getLines();
        for (Line line : lines) {
            if (line.isDrawable()) {
                List<Number> x = new ArrayList<Number>();
                List<Double> y = new ArrayList<Double>();
                List<String> xString = new ArrayList<String>();
                int index = 0;
                for (Point point : line.getPoints()) {
                    x.add(index);
                    xString.add(point.getxCaption());
                    Double yNumber = point.getyValues();
                    y.add(yNumber);
                    index++;
                }

                // Turn the above arrays into XYSeries:
                XYSeries series1 = new SimpleXYSeries(
                        x, y,
                        "Obw�d brzucha");                             // Set the display title of the series

                // Create a formatter to use for drawing a series using LineAndPointRenderer:
                LineAndPointFormatter series1Format = new LineAndPointFormatter(
                        Color.rgb(0, 200, 0),                   // line color
                        Color.rgb(0, 200, 0),                   // point color
                        R.color.none, new PointLabelFormatter());              // fill color (optional)

                // Add series1 to the xyplot:
                mMultitouchPlot.addSeries(series1, series1Format);
                mMultitouchPlot.getGraphWidget().setDomainValueFormat(new GraphXLabelFormat(xString));
                mMultitouchPlot.setRangeBoundaries(0, findMaxValue(y), BoundaryMode.FIXED);
                mMultitouchPlot.setDomainBoundaries(0, 2.2, BoundaryMode.FIXED);
            }
        }
        mMultitouchPlot.invalidate();
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
        final RelativeLayout interval = (RelativeLayout) findViewById(R.id.update_interval);
        final EditText loginText = (EditText) findViewById(R.id.login_input);
        final EditText passwordText = (EditText) findViewById(R.id.password_input);
        authorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authorization.isChecked()){
                    login.setVisibility(View.VISIBLE);
                    loginText.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    passwordText.setVisibility(View.VISIBLE);
                } else {
                    login.setVisibility(View.GONE);
                    loginText.setVisibility(View.GONE);
                    password.setVisibility(View.GONE);
                    passwordText.setVisibility(View.GONE);
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
        mMinutesUpdateInterval = (TextView) findViewById(R.id.interval_minutes);
        final SlidingDrawer historyDrawer = (SlidingDrawer) findViewById(R.id.bottom_drawer);
        historyDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                historyDrawer.bringToFront();
            }
        });
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
                if (mCurrentApp.getCharts().size() == 0){
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
                }else {
                    SlidingDrawer chartSlidingDrawer = (SlidingDrawer) findViewById(R.id.charts_drawer);
                    if (chartSlidingDrawer.isOpened()){
                        chartSlidingDrawer.animateClose();
                    } else {
                        chartSlidingDrawer.animateOpen();
                    }

                }
            }
        });
    }

    private double findMaxValue(List<Double> y){
        double max = 0;
        for (Double n : y){
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
                new HistoryGetter().execute("http://server04.test.anotheria.net:8999/moskito-control/rest/history");
                new ChartsGetter().execute("http://server04.test.anotheria.net:8999/moskito-control/rest/charts/points");
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
                if (parent.isGroupExpanded(groupPosition)){
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
            new ChartsGetter().execute("http://server04.test.anotheria.net:8999/moskito-control/rest/charts/points");
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
            initMultitouchPlot();
        }
    }
}
