package com.example.hamed.obdappnewmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    ImageButton switchact;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bluebutton();
        Mapbutton ();
        fuel_button ();
        speed_button ();
        support_button ();
        temp_button ();

        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);

    }

   public void Bluebutton (){

       switchact =(ImageButton)findViewById(R.id.BluetoothButton);
       switchact.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View view) {
               Intent blueact = new Intent(view.getContext(),BluetoothActivity.class);

               //Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(blueact);
           }
       });
   }

    public void Mapbutton (){

        switchact =(ImageButton)findViewById(R.id.mapbutton);
        switchact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent Mapact = new Intent(view.getContext(),MapsActivity.class);

                //Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Mapact);
            }
        });
    }

    public void fuel_button (){

        switchact =(ImageButton)findViewById(R.id.imageButton3);
        switchact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent Fuelact = new Intent(view.getContext(),fuel_el.class);

                //Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Fuelact);
            }
        });
    }

    public void speed_button (){

        switchact =(ImageButton)findViewById(R.id.imageButton2);
        switchact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent Fuelact = new Intent(view.getContext(),speed.class);

                //Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Fuelact);
            }
        });
    }

    public void support_button (){

        switchact =(ImageButton)findViewById(R.id.imageButton6);
        switchact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent Fuelact = new Intent(view.getContext(),support.class);

                //Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Fuelact);
            }
        });
    }

    public void temp_button (){

        switchact =(ImageButton)findViewById(R.id.imageButton4);
        switchact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent Fuelact = new Intent(view.getContext(),temp.class);

                //Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Fuelact);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
