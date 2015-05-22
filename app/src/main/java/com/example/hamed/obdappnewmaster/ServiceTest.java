package com.example.hamed.obdappnewmaster;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Prakash on 22-05-2015.
 */
public class ServiceTest extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
