package com.example.goputapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.sdk.user.UserApiClient;

import org.w3c.dom.Text;

import java.util.prefs.Preferences;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MyPageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MyPageActivityTAG";
    private FirebaseUser fUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbClubInfo, dbUsers;
    private TextView userName, myClubName;
    private Button logOut;
    private LinearLayout clubSetting, matchingSetting, accountSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userName = (TextView) findViewById(R.id.userName);
        dbUsers = database.getReference("Users");
        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    GoPutUser goPutUser = dataSnapshot.getValue(GoPutUser.class);
                    if(dataSnapshot.getKey().equals(fUser.getUid())){
                        userName.setText(goPutUser.registName);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        logOut = (Button) findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃
                mAuth.signOut();
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        return null;
                    }
                });
                //디바이스에 저장된 데이터 모두 삭제
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                preferencesEditor.clear();
                preferencesEditor.apply();

                //알림 채널 삭제
                NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String id = "클럽초대알림";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.deleteNotificationChannel(id);
                }
                Intent intent = new Intent(getApplicationContext(), LoginRegistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        myClubName = (TextView) findViewById(R.id.myClubName);
        dbClubInfo = database.getReference("ClubInfo");
        dbClubInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ClubInfo clubInfo = dataSnapshot.getValue(ClubInfo.class);
                    if (clubInfo.getId().equals(fUser.getUid())) {
                        myClubName.setText(clubInfo.getClubName());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        clubSetting = (LinearLayout) findViewById(R.id.clubSetting);
        matchingSetting = (LinearLayout) findViewById(R.id.matchingSetting);
        accountSetting = (LinearLayout) findViewById(R.id.accountSetting);
        clubSetting.setOnClickListener(this);
        matchingSetting.setOnClickListener(this);
        accountSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clubSetting:
                startActivity(new Intent(v.getContext(),Mypage_ClubSetting.class));
                break;
            case R.id.matchingSetting:
                startActivity(new Intent(v.getContext(),MyPage_MatchList.class));
                break;
            case R.id.accountSetting:
                startActivity(new Intent(v.getContext(),MyPage_AccountSetting.class));
                break;
        }
    }
}