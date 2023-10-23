package com.example.myapplication;

/*Imports Libraries*/
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
//        //..Asking for permissions
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
//
//        //.. Clearing the lastest app data preferences
//        this.getSharedPreferences("MyPref", 0).edit().clear().apply();
//
//

        /*The onCreate method is responsible to initialize the activity*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);

        //..initializes the buttons
        Button btnStart = findViewById(R.id.btnStart);
        //..set what happens when the user clicks
        btnStart.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(),"Starting...",Toast.LENGTH_LONG).show();
                Intent Ac2 = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(Ac2);
                finish();
            }
        });

        //..initializes the buttons
        Button btnRealTime = findViewById(R.id.realTime);
        //..set what happens when the user clicks
        btnRealTime.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(),"Starting...",Toast.LENGTH_LONG).show();
                Intent Ac3 = new Intent(MainActivity2.this, MainActivity3.class);
                startActivity(Ac3);
                finish();
            }
        });

        Button btnExit = findViewById(R.id.btnExit);
        //..set what happens when the user clicks
        btnExit.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(),"Exiting...",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}


