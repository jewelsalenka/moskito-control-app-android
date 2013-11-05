package com.stub.entity;

import com.example.moskito_control_app_android.R;

import java.io.Serializable;

/**
 * User: Olenka Shemshey
 * Date: 21.07.13
 */
public enum State implements Serializable{
    GREEN(R.drawable.green, R.color.green),
    YELLOW(R.drawable.yellow, R.color.yellow),
    ORANGE(R.drawable.orange, R.color.orange),
    RED(R.drawable.red, R.color.red),
    PURPLE(R.drawable.purple, R.color.purple),
    NONE(R.drawable.none, R.color.none);

    private final int mDrawableId;
    private final int mColorId;

    private State(int drawableId, int colorId){
        mDrawableId = drawableId;
        mColorId = colorId;
    }
    public int getColorDrawableId() {
        return mDrawableId;
    }

    public int getColorId(){
        return mColorId;
    }

}
