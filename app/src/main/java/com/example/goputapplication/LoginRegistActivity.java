package com.example.goputapplication;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.goputapplication.RegistFinsihActivity.registList;

public class LoginRegistActivity extends AppCompatActivity {
    public static final String CHANNER_1_ID = "클럽초대알림";
    private final String TAG="LOGINREGISTACTIVITYTAG";
    private TextView loginbtn,registbtn;
    private FirebaseAuth mAuth;
    public static Activity finishFromLogin;
    private FirebaseDatabase database;
    private DatabaseReference dbClubInfo;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dbClubInfo = database.getReference("ClubInfo");

        checkCurrentUser();
        createNotificationChannels();
        setContentView(R.layout.activity_login_regist);
        isIntro();
        finishFromLogin = LoginRegistActivity.this;
        loginbtn = (TextView)findViewById(R.id.loginbtn);
        registbtn = (TextView)findViewById(R.id.registbtn);



        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        registbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registList.add(LoginRegistActivity.this);
                Intent intent = new Intent(getApplicationContext(), RegistActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void checkCurrentUser(){
        if (user != null) {
            dbClubInfo.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() == null){
                        Toast.makeText(getApplicationContext(), "등록된 클럽이 없습니다!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginRegistActivity.this, MakeOrJoinActivity.class));
                    }else{
                        SetClubInfo();
                        Toast.makeText(getApplicationContext(), "환영합니다!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginRegistActivity.this, InfoAcitivity.class));
                    }
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else{
            Toast.makeText(this, "로그인 하러가기", Toast.LENGTH_SHORT).show();
        }
    }
    private void isIntro() {
        startActivity(new Intent(this, SplashActivity.class));
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            NotificationChannel channel = new NotificationChannel(
                    CHANNER_1_ID,
                    "클럽초대알림",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("클럽초대알림입니다.");

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            mNotificationManager.createNotificationChannel(channel);
        }
    }
    public void SetClubInfo() {
        dbClubInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ClubInfo clubInfoReset = dataSnapshot.getValue(ClubInfo.class);
                        if (clubInfoReset.getId()!=null && clubInfoReset.getId().equals(user.getUid()) ) {
//                            clubInfo.setClubInfo(dataSnapshot.getKey(), user.getUid(), clubInfoReset.getClubName(), clubInfoReset.getMajor(), clubInfoReset.getAgeGroup(), clubInfoReset.getAbility());
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putString("myClubKey",dataSnapshot.getKey());
                            edit.putString("myClubUid",user.getUid());
                            edit.putString("myClubName",clubInfoReset.getClubName());
                            edit.putString("myClubMajor",clubInfoReset.getMajor());
                            edit.putString("myClubAgeGroup",clubInfoReset.getAgeGroup());
                            edit.putString("myClubAbility",clubInfoReset.getAbility());
                            edit.apply();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}