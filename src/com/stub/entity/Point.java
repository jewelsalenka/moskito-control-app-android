package com.stub.entity;

import java.io.Serializable;

/**
 * User: Olenka Shemshey
 * Date: 14.10.13
 */
public class Point implements Serializable{
    private String xCaption;
    private double yValues;
    private long timestamp;

    public Point(String xCaption, double yValues) {
        this.xCaption = xCaption;
        this.yValues = yValues;
    }

    public Point(String xCaption, double yValues, long debugTs) {
        this.xCaption = xCaption;
        this.yValues = yValues;
        this.timestamp = debugTs;
    }

    public String getxCaption() {
        return xCaption;
    }

    public double getyValues() {
        return yValues;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
