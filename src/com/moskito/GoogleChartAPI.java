package com.moskito;

/**
 * User: Olenka Shemshey
 * Date: 22.12.13
 */
public class GoogleChartAPI {
    public static final String CONTENT =
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
                    + "    <head>"
                    + "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
                    + "            <script type='application/javascript'>"
                    + "                function loadGraph() {"
                    + "                    var frm = document.getElementById('post_form'); "
                    + "                    if (frm) { "
                    + "                        frm.submit(); "
                    + "                    } "
                    + "                } "
                    + "            </script>"
                    + "    </head>"
                    + "    <body onload=\"loadGraph()\">"
                    + "        <form action='https://chart.googleapis.com/chart' method='POST' id='post_form'>"
                    + "            <input type='hidden' name='cht' value='lc'/>"
                    + "            <input type='hidden' name='chs' value='300x200'/>"
                    + "            <input type='hidden' name='chd' value='%s'/>"
                    + "            <input type='hidden' name='chxr' value='%s'/>"
                    + "            <input type='hidden' name='chds' value='%s'/>"
                    + "            <input type='hidden' name='chco' value='%s'/>"
                    + "            <input type='hidden' name='chxt' value='x,y'/>"
                    + "            <input type='hidden' name='chxl' value='%s'/>"
                    + "            <input type='hidden' name='chls' value='3,1,0|3,1,0'/>"
                    + "            <input type='hidden' name='chg' value='0,6.67,5,5'/>"
                    + "            <input type='submit'/>"
                    + "        </form>"
                    + "    </body>"
                    + "</html>";
}
