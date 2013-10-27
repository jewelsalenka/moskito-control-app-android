package com.stub.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 14.10.13
 */
public class Chart implements Serializable{
    private String name;
    private List<Line> lines;

    public Chart(String name) {
        this.name = name;
        lines = new ArrayList<Line>();
    }

    public Chart(String name, List<Line> lines) {
        this.name = name;
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void addLine(Line line){
        lines.add(line);
    }

}
