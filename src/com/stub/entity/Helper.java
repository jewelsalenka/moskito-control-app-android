package com.stub.entity;

import org.moskito.control.requester.Requester;
import org.moskito.control.requester.config.RequesterConfiguration;
import org.moskito.control.requester.data.DataProvider;
import org.moskito.control.requester.data.HistoryResponse;
import org.moskito.control.requester.data.StatusResponse;
import org.moskito.control.requester.parser.ResponseParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class Helper {

    private List<Application> applications;
    StatusResponse statusResponse;
    DataProvider dataProvider;

    public Helper(String url) throws IOException {
        createConnection(url);
    }

    private void createConnection(String url){
        RequesterConfiguration configuration = new RequesterConfiguration();
        configuration.setConnectTimeout(100000);
        configuration.setReadTimeout(100000);
        Requester requester = new Requester(configuration);
        ResponseParser parser = new ResponseParser();
        dataProvider = new DataProvider(requester, parser);
        statusResponse = dataProvider.getStatusResponse(url);
    }

    public List<HistoryItem> getHistory(String url, String appName) throws IOException{
        List<HistoryItem> history = new ArrayList<HistoryItem>();
        HistoryResponse historyResponse = dataProvider.getHistoryResponse(url, appName);
        for(org.moskito.control.requester.data.HistoryItem historyItem : historyResponse.getHistoryItems()) {
            String componentName = historyItem.getComponentName();
            State oldState = State.valueOf(historyItem.getOldStatus().toString());
            State newState = State.valueOf(historyItem.getNewStatus().toString());
            String info = new String();
            for(String message : historyItem.getNewMessages()) {
                info += message;
            }
            Date date = new Date(historyItem.getTimestamp());
            HistoryItem item = new HistoryItem(componentName, oldState, newState, date, info);
            history.add(item);
        }
        return history;
    }

    public List<Application> getAllApps(){
        List<Application> appList = new ArrayList<Application>();
        for (org.moskito.control.requester.data.Application app: statusResponse.getApplications())  {
            String appName = app.getName();
            State appColor = State.valueOf(app.getApplicationColor().toString());
            Application application = new Application(appName, appColor);
            for(org.moskito.control.requester.data.Component component : app.getComponents()) {
                String name = component.getName();
                String info = new String();
                for(String message : component.getMessages()) {
                    info+=message;
                }
                Date date = new Date(component.getLastUpdateTimestamp());
                State state = State.valueOf(component.getColor().toString());
                Component server = new Component(name, info, date, state);
                application.addServer(server);
            }
            appList.add(application);
        }
        return appList;
    }
}
