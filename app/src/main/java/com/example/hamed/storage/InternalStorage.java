package com.example.hamed.storage;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Niclas on 5/13/15.
 * Class that saves data to local storage in a private file that only this app can access
 */
public class InternalStorage {

    public static final String FILE_NAME = "current_recording.txt";

    /**
     *
     * @param position as custom data bearing object
     * @param con context of caller
     */
    public static void appendToFile(Position position, Context con){
        FileOutputStream fos;
        String positionString = position.toString();
        String[] positionParts = positionString.split(":");
        String latLng = positionParts[1];
        String speed = positionParts[2];

        Log.d("InternalStorage latlnt", latLng);

        try {
            fos = con.openFileOutput(FILE_NAME, Context.MODE_APPEND);

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(latLng + ":" + speed + ";" +"\n");
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
    /**
     *
     * @param data
     * @param filename
     * @param con
     * @throws Exception
     */
    public static void save(String data, String filename, Context con) throws Exception{
        Log.d("INTERNAL STORAGE " , data);

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

    /**
     *
     * @param con context of caller
     * @return Arraylist with sanitized data, see Position.java for reference
     */
    public static ArrayList<Position> loadData(Context con) {
        FileInputStream in;
        String input = "";

        try {
            in = con.openFileInput(FILE_NAME);
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
        //String[] resultWithoutHeader = input.split("#");
        return sanitizeLoadedData(input);
    }

    // Helper method to clean data
    private static ArrayList<Position> sanitizeLoadedData(String data) {
        ArrayList<Position> sanitizedData = new ArrayList<>();
              if (data != null){
            String[] mapEntries = data.split(";");
            for (String str : mapEntries) {
                String[] posAndSpeed = str.split("speed");
                String lat = posAndSpeed[0].split(",")[0].split("\\(")[1];
                String lng = posAndSpeed[0].split(",")[1].split("\\)")[0];
                String speed = posAndSpeed[1].split(":")[1].substring(1);

                Log.d("mark Lat", lat);
                Log.d("mark Lng", lng);
                Log.d("mark speed", speed);

                LatLng latLng = new LatLng(Float.parseFloat(lat), Float.parseFloat(lng));
                Position pos = new Position(latLng, Integer.valueOf(speed));
                sanitizedData.add(pos);
            }
        }
        return sanitizedData;
    }
}
