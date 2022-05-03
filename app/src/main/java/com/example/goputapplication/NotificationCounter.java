package com.example.goputapplication;

import android.view.View;
import android.widget.TextView;


public class NotificationCounter {
    private TextView notificationNumber;
    private int notification_number_counter=1;

    public NotificationCounter(View view){
        notificationNumber = view.findViewById(R.id.notificationBtn);
    }

    public int setNumber(){

        return notification_number_counter;
    }
}
