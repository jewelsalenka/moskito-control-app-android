package com.stub.entity;

import android.graphics.Color;

import java.io.Serializable;
import java.util.Date;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class Change implements Serializable{
    private Server server;
    private ServerState oldColor;
    private ServerState newColor;
    private Date date;


    public Change(Server server, ServerState oldColor, ServerState newColor, Date date) {
        this.server = server;
        this.oldColor = oldColor;
        this.newColor = newColor;
        this.date = date;
    }

    public Server getServer() {
        return server;
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
}
