package com.stub.entity;

import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * User: Olenka Shemshey
 * Date: 14.10.13
 */
public class Chart implements Serializable{
    private String name;
    private List<Line> lines;

    public Chart(String name) {
        this.name = name;
        lines = new ArrayList<Line>();
    }

    public Chart(String name, List<Line> lines) {
        this.name = name;
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void addLine(Line line){
        lines.add(line);
    }

    //todo remove after charts get from network
    public static Chart createStubChart(){
        Chart chart = new Chart("Sessions 100");
        Line line = new Line("SessionCount CurAbsolute@localhost");
        Random random = new Random();
        long currentMillis = System.currentTimeMillis();
        long startMillis = currentMillis - 10800000;
        for (long millis = startMillis; millis < currentMillis; millis += 300000){
            //String time = new SimpleDateFormat("HH:mm").format(new Date(millis));
            Point point = new Point(new Date(millis), random.nextInt(40));
            line.addPoint(point);
        }
        chart.addLine(line);
        return chart;
    }
    
}
