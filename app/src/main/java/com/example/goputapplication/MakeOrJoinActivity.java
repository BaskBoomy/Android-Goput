package com.example.goputapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import static com.example.goputapplication.MakeMatch2Activity.matchActivityList;

public class MakeOrJoinActivity extends AppCompatActivity {

    LinearLayout makeclubbtn;
    LinearLayout joinclubbtn;

    int valueFromLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_or_join);

        Intent regist1intent = getIntent();
        valueFromLogin = regist1intent.getIntExtra("valueFromLogin",0);


        makeclubbtn = (LinearLayout)findViewById(R.id.makeclubbtn);
        makeclubbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchActivityList.add(MakeOrJoinActivity.this);
                startActivity(new Intent(getApplicationContext(), MakeClubActivity.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        joinclubbtn = (LinearLayout)findViewById(R.id.joinclubbtn);
        joinclubbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchClubActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(valueFromLogin == 1001){
            return;
        }
        else{
            super.onBackPressed();
        }
    }
}