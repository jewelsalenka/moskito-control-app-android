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
    private final String mName;
    private final List<Point> mPoints;
    private final LineColors mColor;
    private volatile boolean mIsDrawable = true;

    public Line(String name, List<Point> points, int i) {
        mName = name;
        mPoints = Collections.synchronizedList(points);
        mColor = LineColors.values()[i-1];
    }

    public Line(String name, int i) {
        mName = name;
        mPoints = Collections.synchronizedList(new ArrayList<Point>());
        mColor = LineColors.values()[i-1];
    }

    public String getName() {
        return mName;
    }

    public List<Point> getPoints() {
        return new ArrayList<Point>(mPoints);
    }

    public void addPoint(Point point){
        mPoints.add(point);
    }

    public boolean isDrawable() {
        return mIsDrawable;
    }

    public void setDrawable(boolean drawable) {
        mIsDrawable = drawable;
    }

    public LineColors getColor() {
        return mColor;
    }
}
