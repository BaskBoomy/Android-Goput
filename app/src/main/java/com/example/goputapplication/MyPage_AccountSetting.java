package com.example.goputapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyPage_AccountSetting extends AppCompatActivity {
    private static final String TAG = "MyPage_AccountSetting";
    private FirebaseUser fUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbUsers;
    private TextView accountInfo, userName, userBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page__account_setting);
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        accountInfo = (TextView) findViewById(R.id.accountInfo);
        userName = (TextView) findViewById(R.id.userName);
        userBirth = (TextView) findViewById(R.id.userBirth);

        dbUsers = database.getReference("Users");
        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    GoPutUser goPutUser = dataSnapshot.getValue(GoPutUser.class);
                    if(mAuth.getUid().equals(dataSnapshot.getKey())){
                        //고풋계정, 카카오계정 여부
                        String accountId = mAuth.getUid();
                        Log.d(TAG,"앞5 : "+accountId.substring(0,4));
                        if(accountId.substring(0,5).equals("kakao")){
                            accountInfo.setText("카카오계정");
                        }else{
                            accountInfo.setText("Goput계정");
                        }
                        userName.setText(goPutUser.registName);
                        userBirth.setText("설정해야됨!");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}