package com.example.goputapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Mypage_ClubSetting extends AppCompatActivity {
    private FirebaseUser fUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbClubInfo,dbMember;

    private TextView myClubName, myClubMemberCount, myClubMajor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage__club_setting);
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        myClubName = (TextView) findViewById(R.id.myClubName);
        myClubMemberCount = (TextView) findViewById(R.id.myClubMemberCount);
        myClubMajor = (TextView) findViewById(R.id.myClubMajor);

        dbClubInfo = database.getReference("ClubInfo");
        dbMember = database.getReference("Member");
        dbClubInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ClubInfo clubInfo = dataSnapshot.getValue(ClubInfo.class);
                    if (clubInfo.getId().equals(fUser.getUid())) {
                        myClubName.setText(clubInfo.getClubName());
                        myClubMajor.setText(clubInfo.getMajor());
                        String myClubKey = dataSnapshot.getKey();
                        dbMember.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                    if (myClubKey.equals(dataSnapshot1.getKey())) {
                                        myClubMemberCount.setText(String.valueOf(dataSnapshot1.getChildrenCount())+"명");
                                        break;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}