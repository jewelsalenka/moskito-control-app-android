package com.stub.entity;


import org.moskito.control.restclient.DataProvider;
import org.moskito.control.restclient.config.RequesterConfiguration;
import org.moskito.control.restclient.data.response.ChartsResponse;
import org.moskito.control.restclient.data.response.HistoryResponse;
import org.moskito.control.restclient.data.response.StatusResponse;
import org.moskito.control.restclient.http.Requester;
import org.moskito.control.restclient.parser.ResponseParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class Helper {
    private static final int CONNECT_TIMEOUT = 100000;
    private static final int READ_TIMEOUT = 100000;
    private final DataProvider dataProvider;

    public Helper() {
        RequesterConfiguration configuration = new RequesterConfiguration();
        configuration.setConnectTimeout(CONNECT_TIMEOUT);
        configuration.setReadTimeout(READ_TIMEOUT);
        Requester requester = new Requester(configuration);
        ResponseParser parser = new ResponseParser();
        dataProvider = new DataProvider(requester, parser);
    }

    public List<HistoryItem> getHistory(String url, String appName) {
        List<HistoryItem> history = new ArrayList<HistoryItem>();
        HistoryResponse historyResponse;
        synchronized (dataProvider) {
            historyResponse = dataProvider.getHistoryResponse(url, appName);
        }
        for(org.moskito.control.restclient.data.HistoryItem historyItem : historyResponse.getHistoryItems()) {
            String componentName = historyItem.getComponentName();
            State oldState = State.valueOf(historyItem.getOldStatus().toString());
            State newState = State.valueOf(historyItem.getNewStatus().toString());
            List<String> info = new ArrayList<String>();
            for(String message : historyItem.getNewMessages()) {
                info.add(message);
            }
            Date date = new Date(historyItem.getTimestamp());
            HistoryItem item = new HistoryItem(componentName, oldState, newState, date, info);
            history.add(item);
        }
        return history;
    }

    public List<Application> getAllApps(String url) {
        List<Application> appList = new ArrayList<Application>();
        StatusResponse statusResponse;
        synchronized (dataProvider) {
            statusResponse = dataProvider.getStatusResponse(url);
        }
        if (statusResponse == null) return appList;
        for (org.moskito.control.restclient.data.Application app: statusResponse.getApplications())  {
            String appName = app.getName();
            State appColor = State.valueOf(app.getApplicationColor().toString());
            Application application = new Application(appName, appColor);
            for(org.moskito.control.restclient.data.Component component : app.getComponents()) {
                String name = component.getName();
                StringBuilder info = new StringBuilder();
                for(String message : component.getMessages()) {
                    info.append(message);
                }
                Date date = new Date(component.getLastUpdateTimestamp());
                State state = State.valueOf(component.getColor().toString());
                Component server = new Component(name, info.toString(), date, state);
                application.addComponent(server);
            }
            appList.add(application);
        }
        return appList;
    }

    public List<Chart> getCharts(String url, String appName) {
        List<Chart> charts = new ArrayList<Chart>();
        ChartsResponse chartResponse;
        synchronized (dataProvider){
            chartResponse = dataProvider.getChartsResponse(url, appName);
        }
        for (org.moskito.control.restclient.data.Chart jsonChart : chartResponse.getCharts()){
            String chartName = jsonChart.getName();
            List<Line> lines = new ArrayList<Line>();
            List<org.moskito.control.restclient.data.Line> jsonLines = jsonChart.getLines();
            for (org.moskito.control.restclient.data.Line jsonLine : jsonLines){
                String lineNames = jsonLine.getName();
                List<Point> points = new ArrayList<Point>();
                List<org.moskito.control.restclient.data.Point> jsonPoints = jsonLine.getPoints();
                for (org.moskito.control.restclient.data.Point jsonPoint : jsonPoints){
                    long timestamp = jsonPoint.getTimestamp();
                    String x = jsonPoint.getX();
                    double y = Double.parseDouble(jsonPoint.getY());
                    Point point = new Point(x,y,timestamp);
                    points.add(point);
                }
                Line line = new Line(lineNames, points);
                lines.add(line);
            }
            Chart chart = new Chart(chartName, lines);
            charts.add(chart);
        }
        return charts;
    }
}
