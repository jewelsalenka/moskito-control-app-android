package com.stub.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Olenka Shemshey
 * Date: 14.10.13
 */
public class Chart implements Serializable{
    private final String mName;
    private final List<Line> mLines;

    public Chart(String name) {
        mName = name;
        mLines = Collections.synchronizedList(new ArrayList<Line>());
    }

    public Chart(String name, List<Line> lines) {
        mName = name;
        mLines = lines;
    }

    public String getName() {
        return mName;
    }

    public List<Line> getLines() {
        return new ArrayList<Line>(mLines);
    }

    public void addLine(Line line){
        mLines.add(line);
    }
}
