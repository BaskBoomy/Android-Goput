package com.example.goputapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class RegistFinsihActivity extends AppCompatActivity {
    public static ArrayList<Activity> registList = new ArrayList<Activity>();
    Button gotochoosebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registfinish);
        gotochoosebtn = (Button)findViewById(R.id.gotochoosebtn);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        gotochoosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.sendEmailVerification();
                for(int i = 0; i < registList.size(); i++) registList.get(i).finish();
                finishAndRemoveTask();
                Toast.makeText(getApplicationContext(),"이메일 인증후 사용해주세요!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }


}
