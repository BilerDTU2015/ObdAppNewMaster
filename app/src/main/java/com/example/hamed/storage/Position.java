package com.example.hamed.storage;

import android.os.Parcel;
import android.os.Parcelable;
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
    private int speed;
    private String EffectPower;

    public Position(LatLng position, int speed){
        this.position = position;
        this.speed = speed;
    }


    public LatLng getLatLng(){
        return position;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString(){
        String returnValue = position.toString() + " speed : " + speed;
        Log.d("POSITION TO STRING", returnValue);
        return returnValue;
    }
}
