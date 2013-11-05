package com.stub.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class HistoryItem implements Serializable{
    private final String mComponentName;
    private final State mOldColor;
    private final State mNewColor;
    private final List<String> mMessages;
    private final Date mDate;

    public HistoryItem(String componentName, State oldColor, State newColor, Date date, List<String> info) {
        mComponentName = componentName;
        mOldColor = oldColor;
        mNewColor = newColor;
        mDate = date;
        mMessages = new ArrayList<String>(info);
    }

    public String getComponentName() {
        return mComponentName;
    }

    public int getOldColorId() {
        return mOldColor.getColorDrawableId();
    }

    public int getNewColorId() {
        return mNewColor.getColorDrawableId();
    }

    public Date getDate() {
        return mDate;
    }

    public List<String> getMessages() {
        return new ArrayList<String>(mMessages);
    }
}
