package com.stub.entity;

import org.moskito.control.requester.Requester;
import org.moskito.control.requester.config.RequesterConfiguration;
import org.moskito.control.requester.data.DataProvider;
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

    public Helper(String url) throws IOException {
        createConnection(url);
    }

    private void createConnection(String url){
        RequesterConfiguration configuration = new RequesterConfiguration();
        configuration.setConnectTimeout(100000);
        configuration.setReadTimeout(100000);
        Requester requester = new Requester(configuration);
        ResponseParser parser = new ResponseParser();
        DataProvider dataProvider = new DataProvider(requester, parser);
        statusResponse = dataProvider.getStatusResponse(url);
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
                    info.concat(message);
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
