package com.stub.entity;

import com.example.moskito_control_app_android.R;

/**
 * User: Olenka Shemshey
 * Date: 04.12.13
 */
public enum LineColors {
    GREEN(1),
    YELLOW(2),
    PURPLE(3),
    ORANGE(4),
    RED(5);

    private final int mDrawableId;
    private final String mColor;
    public static final int LENGTH = LineColors.values().length;

    private LineColors(int i){
        switch (i){
            case 1 :
                mDrawableId = R.drawable.green;
                mColor = "94CC19";
                break;
            case 2 :
                mDrawableId = R.drawable.yellow;
                mColor = "DDCE00";
                break;
            case 3 :
                mDrawableId = R.drawable.purple;
                mColor = "E153D6";
                break;
            case 4 :
                mDrawableId = R.drawable.orange;
                mColor = "E18400";
                break;
            case 5:
                //
            default:
                mDrawableId = R.drawable.red;
                mColor = "FC3E39";
        }
    }

    public int getColorDrawableId() {
        return mDrawableId;
    }

    public String getColor(){
        return mColor;
    }
}
