package com.stub.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class Helper {

    private List<Application> applications;

    public Helper(){
        applications = new ArrayList<Application>();
        Application app1 = new Application("SecondApp", AppColor.LIME);
        Application app2 = new Application("FirstApp", AppColor.MAGENTA);
        String info = "Can't connect to the FirstApp.server04";
        Date date1 = new Date();
        date1.setYear(2013);
        date1.setMonth(7);
        date1.setDate(23);
        date1.setHours(12);
        date1.setMinutes(23);
        date1.setSeconds(47);
        Server server1 = new Server("localhost", info, date1, ServerState.GREEN);
        Server server2 = new Server("server04", info, date1, ServerState.RED);
        Server server3 = new Server("XYZ", info, date1, ServerState.GREEN);
        app2.addServer(server1);
        app2.addServer(server2);
        app2.addServer(server3);
        Change change1 = new Change(server1, ServerState.GREEN, ServerState.ORANGE, new Date());
        Change change2 = new Change(server2, ServerState.VIOLET, ServerState.RED, new Date());
        Change change3 = new Change(server1, ServerState.ORANGE, ServerState.YELLOW, new Date());
        Change change4 = new Change(server1, ServerState.YELLOW, ServerState.GREEN, new Date());
        app2.addChange(change1);
        app2.addChange(change2);
        app2.addChange(change3);
        app2.addChange(change4);
        applications.add(app1);
        applications.add(app2);

    }

    public List<Application> getAllApps(){
        return applications;
    }
}
