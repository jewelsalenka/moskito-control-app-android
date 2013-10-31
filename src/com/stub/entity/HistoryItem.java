package com.stub.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class HistoryItem implements Serializable{
    private String componentName;
    private State oldColor;
    private State newColor;
    private List<String> mMessages;
    private Date date;

    public HistoryItem(String componentName, State oldColor, State newColor, Date date, List<String> info) {
        this.componentName = componentName;
        this.oldColor = oldColor;
        this.newColor = newColor;
        this.date = date;
        this.mMessages = info;
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

    public List<String> getMessages() {
        return mMessages;
    }
}
