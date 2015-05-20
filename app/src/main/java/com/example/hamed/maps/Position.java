package com.example.hamed.maps;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Niclsa on 5/20/15.
 *
 * This class saves a position and any additional data that we record on this particular
 * point, at the moment just speed, but could be anything and easy to extend
 */
public class Position {

    private LatLng position;
    private String speed;
    private String EffectPower;


    public Position(LatLng position, String speed){
        this.position = position;
        this.speed = speed;
    }

    @Override
    public String toString(){
        String returnValue = "position : " + position.toString() + " speed : " + speed;
        Log.d("POSITION TO STRING", returnValue);
        return returnValue;
    }

}
