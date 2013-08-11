package com.stub.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class HistoryItem implements Serializable{
    private Component component;
    private State oldColor;
    private State newColor;
    private String info;
    private Date date;

    public HistoryItem(Component component, State oldColor, State newColor, Date date, String info) {
        this.component = component;
        this.oldColor = oldColor;
        this.newColor = newColor;
        this.date = date;
        this.info = info;
    }

    public Component getComponent() {
        return component;
    }

    public int getOldColorId() {
        return oldColor.getColorId();
    }

    public int getNewColorId() {
        return newColor.getColorId();
    }

    public Date getDate() {
        return date;
    }

    public String getInfo() {
        return info;
    }
}
