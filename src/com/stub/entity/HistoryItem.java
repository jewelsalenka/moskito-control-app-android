package com.stub.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class HistoryItem implements Serializable{
    private String componentName;
    private State oldColor;
    private State newColor;
    private String info;
    private Date date;

    public HistoryItem(String componentName, State oldColor, State newColor, Date date, String info) {
        this.componentName = componentName;
        this.oldColor = oldColor;
        this.newColor = newColor;
        this.date = date;
        this.info = info;
    }

    public String getComponentName() {
        return componentName;
    }

    public int getOldColorId() {
        return oldColor.getColorDrawableId();
    }

    public int getNewColorId() {
        return newColor.getColorDrawableId();
    }

    public Date getDate() {
        return date;
    }

    public String getInfo() {
        return info;
    }
}
