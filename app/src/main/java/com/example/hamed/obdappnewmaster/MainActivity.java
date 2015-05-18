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
