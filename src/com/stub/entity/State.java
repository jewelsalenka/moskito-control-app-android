package com.stub.entity;

import com.example.moskito_control_app_android.R;

/**
 * User: Olenka Shemshey
 * Date: 21.07.13
 */
public enum State {
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    PURPLE,
    NONE;

    public int getColorDrawableId() {
        switch (this){
            case GREEN:
                return R.drawable.green;
            case YELLOW:
                return R.drawable.yellow;
            case ORANGE:
                return R.drawable.orange;
            case RED:
                return R.drawable.red;
            case PURPLE:
                return R.drawable.violet;
            case NONE:

            default:
                return R.drawable.magenta;
        }
    }

    public int getColorId(){
        switch (this){
            case GREEN:
                return R.color.green;
            case YELLOW:
                return R.color.yellow;
            case ORANGE:
                return R.color.orange;
            case RED:
                return R.color.red;
            case PURPLE:
                return R.color.violet;
            case NONE:

            default:
                return R.color.magenta;
        }
    }

}
