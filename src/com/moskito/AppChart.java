package com.moskito;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.Visibility;
import android.os.Bundle;
import android.renderscript.ProgramFragmentFixedFunction;
import android.text.format.Time;
import android.util.Log;
import com.androidplot.xy.*;
import com.example.moskito_control_app_android.R;
import com.stub.entity.Application;
import com.stub.entity.Chart;
import com.stub.entity.Line;
import com.stub.entity.Point;

import java.util.*;

import static android.renderscript.ProgramFragmentFixedFunction.Builder.Format;

/**
 * User: Olenka Shemshey
 * Date: 15.10.13
 */
public class AppChart extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charts);

        MultitouchPlot multitouchPlot = (MultitouchPlot) findViewById(R.id.multitouchPlot);
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(255, 255, 236));
        backgroundPaint.setStyle(Paint.Style.FILL);
        multitouchPlot.getGraphWidget().setBackgroundPaint(backgroundPaint);
        multitouchPlot.getLegendWidget().setVisible(false);
        multitouchPlot.getGraphWidget().setGridBackgroundPaint(backgroundPaint);
        multitouchPlot.setDomainStepValue(1);

        multitouchPlot.setDomainValueFormat();
        multitouchPlot.setRangeStepValue(1);

        Application app = (Application) getIntent().getExtras().getSerializable(ApplicationActivity.APP_KEY);
        Chart chart = app.getCharts().get(0);
        Line line = chart.getLines().get(0);
        List<Number> x = new ArrayList<Number>();
        List<Number> y = new ArrayList<Number>();
        for (Point point : line.getPoints()){
            Number xNumber = dateToNumber(point.getxCaption());
            x.add(xNumber);
            Number yNumber = point.getyValues();
            y.add(yNumber);
            Log.i("Alenka", "x = " + xNumber + "; y = " + yNumber);
        }

        // Turn the above arrays into XYSeries:
        XYSeries series1 = new SimpleXYSeries(
                x,y,
                "Obw�d brzucha");                             // Set the display title of the series

        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 200, 0),                   // line color
                Color.rgb(0, 200, 0),                   // point color
                R.color.none, new PointLabelFormatter());              // fill color (optional)

        // Add series1 to the xyplot:
        multitouchPlot.addSeries(series1, series1Format);

        // Reduce the number of range labels
        multitouchPlot.setTicksPerRangeLabel(3);

        // By default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
       // multitouchPlot.disableAllMarkup();

        multitouchPlot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        multitouchPlot.setDomainBoundaries(0, 2.2, BoundaryMode.FIXED);
        multitouchPlot.invalidate();
    }

    public Number dateToNumber(Date date){
        Number number;
        int hours = date.getHours();
        int minutes = date.getMinutes();
        number = hours*60 + minutes;
        return number;
    }
}