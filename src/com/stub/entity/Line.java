package com.stub.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 14.10.13
 */
public class Line implements Serializable{
    private final String name;
    private final List<Point> points;
    private volatile boolean drawable = true;

    public Line(String name, List<Point> points) {
        this.name = name;
        this.points = Collections.synchronizedList(points);
    }

    public Line(String name) {
        this.name = name;
        points = Collections.synchronizedList(new ArrayList<Point>());
    }

    public String getName() {
        return name;
    }

    public List<Point> getPoints() {
        return new ArrayList<Point>(points);
    }

    public void addPoint(Point point){
        points.add(point);
    }

    public boolean isDrawable() {
        return drawable;
    }

    public void setDrawable(boolean drawable) {
        this.drawable = drawable;
    }
}
