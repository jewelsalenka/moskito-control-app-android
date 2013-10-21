package com.stub.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 14.10.13
 */
public class Line implements Serializable{
    private String name;
    private List<Point> points;

    public Line(String name, List<Point> points) {
        this.name = name;
        this.points = points;
    }

    public Line(String name) {
        this.name = name;
        points = new ArrayList<Point>();
    }

    public String getName() {
        return name;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point){
        points.add(point);
    }
}
