package com.example.goputapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1600);
                    finish();
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    // onClickScreen(null);
                } catch (Exception e) {
                }
            }
        }).start();
    }
}