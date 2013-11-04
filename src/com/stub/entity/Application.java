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
    private final String name;
    private final State color;
    private final List<Component> components;
    private final List<HistoryItem> history;
    private final List<Chart> charts;

    public Application(String name, State color) {
        this.name = name;
        this.color = color;
        components = Collections.synchronizedList(new ArrayList<Component>());
        history = Collections.synchronizedList(new ArrayList<HistoryItem>());
        charts = Collections.synchronizedList(new ArrayList<Chart>());
    }

    public void addComponent(Component component){
        components.add(component);
    }

    public void setHistory(List<HistoryItem> history){
        this.history.clear();
        this.history.addAll(history);
    }

    public void addCharts(List<Chart> charts){
        this.charts.addAll(charts);
    }
    public String getName() {

        return name;
    }

    public State getColor() {
        return color;
    }

    public List<Component> getComponents() {
        synchronized (components){
            return new ArrayList<Component>(components);
        }
    }

    public List<HistoryItem> getHistory() {
        synchronized (history){
            return new ArrayList<HistoryItem>(history);
        }
    }

    public List<Chart> getCharts() {
        synchronized (charts){
            return new ArrayList<Chart>(charts);
        }
    }
}
