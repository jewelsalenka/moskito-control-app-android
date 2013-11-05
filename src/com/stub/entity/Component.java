package com.stub.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class Component implements Serializable {
    private final String mName;
    private final String mInfo;
    private final Date mDate;
    private final State mState;

    public Component(String name, String info, Date date, State state) {
        this.mName = name;
        this.mInfo = info;
        this.mDate = date;
        this.mState = state;
    }

    public String getName() {
        return mName;
    }

    public String getInfo() {
        return mInfo;
    }

    public Date getDate() {
        return mDate;
    }

    public State getState() {
        return mState;
    }
}

