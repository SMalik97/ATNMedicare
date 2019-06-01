package com.atnmedicareapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        //after animation go to home page
        new Timer().schedule(new TimerTask(){
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        // do your work here
                        Intent h=new Intent(getApplicationContext(),Home.class);
                        startActivity(h);
                    }
                });
            }
        }, 3000);
    }
}
