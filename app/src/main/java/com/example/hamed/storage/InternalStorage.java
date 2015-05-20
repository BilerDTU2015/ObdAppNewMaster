package com.example.hamed.storage;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.hamed.maps.Position;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

/**
 * Created by Niclas on 5/13/15.
 * Class that saves data to local storage in a private file that only this app can access
 */
public class InternalStorage {



    public static void appendToFile(Position position, String filename, Context con){

//        String baseFolder;
//// check if external storage is available
//        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            baseFolder = con.getExternalFilesDir(null).getAbsolutePath();
//        }
//// revert to using internal storage
//        else {
//            baseFolder = con.getFilesDir().getAbsolutePath();
//        }


        FileOutputStream fos;
        try {
            fos = con.openFileOutput(filename, Context.MODE_APPEND);

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(position);
            oos.close();
        } catch (FileNotFoundException e) {
            Log.d("STORAGE", "Could not open fileOutput");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("STORAGE", "IOError");
            e.printStackTrace();
        }
    }

    // data can easily be changes to a custom class holding the data
    public static void save(String[] data, String filename, Context con){
        FileOutputStream fos;
        try {
            fos = con.openFileOutput(filename, Context.MODE_PRIVATE);

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
        } catch (FileNotFoundException e) {
            Log.d("STORAGE", "Could not open fileOutput");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("STORAGE", "IOError");
            e.printStackTrace();
        }
    }

    public static String loadData(String filename, Context con) {
        FileInputStream in;
        String input = "";

        try {
            in = con.openFileInput(filename);
            if (in != null) {
                InputStreamReader inputreader = new InputStreamReader(in);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";
            try
            {
                while ((line = buffreader.readLine()) != null)
                    input += line;
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return input;
    }

}
