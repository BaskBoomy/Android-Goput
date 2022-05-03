package com.example.goputapplication;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;


//App이랑 똑같은거라서 사용안함 => 추후 삭제
public class KaKaoApplication extends Application {

    public static final String CHANNER_1_ID = "channel1";
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this,"1ffa67a910296b0ef943d0e6c3c88803");

        createNotificationChannels();
    }
    private void createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            NotificationChannel channel = new NotificationChannel(
                    CHANNER_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("This is Channel 1");

            NotificationManager mNotificationManager=
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            mNotificationManager.createNotificationChannel(channel);

        }
    }
}
