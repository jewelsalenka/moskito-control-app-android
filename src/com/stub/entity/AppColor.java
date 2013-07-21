package com.stub.entity;

import com.example.moskito_control_app_android.R;

/**
 * User: Olenka Shemshey
 * Date: 21.07.13
 */
public enum AppColor {
    LIME, MAGENTA;

    public int getColorDrawableId() {
        switch (this){
            case LIME:
                return R.drawable.lime;
            case MAGENTA:
                //fall through
            default:
                return R.drawable.magenta;
        }
    }

    public int getColorId(){
        switch (this){
            case LIME:
                return R.color.lime;
            case MAGENTA:
                //fall through
            default:
                return R.color.magenta;
        }
    }

}
