package com.stub.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class Application implements Serializable{
    private final String mName;
    private final State mColor;
    private final List<Component> mComponents;
    private final List<HistoryItem> mHistory;
    private final List<Chart> mCharts;

    public Application(String name, State color) {
        mName = name;
        mColor = color;
        mComponents = Collections.synchronizedList(new ArrayList<Component>());
        mHistory = Collections.synchronizedList(new ArrayList<HistoryItem>());
        mCharts = Collections.synchronizedList(new ArrayList<Chart>());
    }

    public void addComponent(Component component){
        mComponents.add(component);
    }

    public void setHistory(List<HistoryItem> history){
        this.mHistory.clear();
        this.mHistory.addAll(history);
    }

    public void addCharts(List<Chart> charts){
        this.mCharts.addAll(charts);
    }
    public String getName() {

        return mName;
    }

    public State getColor() {
        return mColor;
    }

    public List<Component> getComponents() {
        synchronized (mComponents){
            return new ArrayList<Component>(mComponents);
        }
    }

    public List<HistoryItem> getHistory() {
        synchronized (mHistory){
            return new ArrayList<HistoryItem>(mHistory);
        }
    }

    public List<Chart> getCharts() {
        synchronized (mCharts){
            return new ArrayList<Chart>(mCharts);
        }
    }
}
