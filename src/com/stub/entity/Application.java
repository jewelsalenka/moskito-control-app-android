package com.stub.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 13.07.13
 */
public class Application implements Serializable{
    private String name;
    private State color;
    private List<Component> components;
    private List<HistoryItem> history;
    private List<Chart> charts;

    public Application(String name, State color) {
        this.name = name;
        this.color = color;
        components = new ArrayList<Component>();
        history = new ArrayList<HistoryItem>();
        charts = new ArrayList<Chart>();
    }

    public void addComponent(Component component){
        components.add(component);
    }

    public void addChange(HistoryItem historyItem){
        history.add(historyItem);
    }

    public void addHistory(List<HistoryItem> history){
        this.history.addAll(history);
    }

    public void addChart(Chart chart){
        charts.add(chart);
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
        return components;
    }

    public List<HistoryItem> getHistory() {
        return history;
    }

    public List<Chart> getCharts() {
        return charts;
    }
}
