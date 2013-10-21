package com.stub.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: Olenka Shemshey
 * Date: 14.10.13
 */
public class Point implements Serializable{
    private Date xCaption;
    private float yValues;
    private String debugTs;

    public Point(Date xCaption, float yValues) {
        this.xCaption = xCaption;
        this.yValues = yValues;
    }

    public Point(Date xCaption, float yValues, String debugTs) {
        this.xCaption = xCaption;
        this.yValues = yValues;
        this.debugTs = debugTs;
    }

    public Date getxCaption() {
        return xCaption;
    }

    public float getyValues() {
        return yValues;
    }

    public String getDebugTs() {
        return debugTs;
    }
}
