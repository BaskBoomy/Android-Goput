package com.example.goputapplication;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.sdk.common.KakaoSdk;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class App extends Application {
    private static final String TAG = "AppTAG";
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, "1ffa67a910296b0ef943d0e6c3c88803");
    }
}
