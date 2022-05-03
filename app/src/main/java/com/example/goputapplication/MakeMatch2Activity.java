package com.example.goputapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.goputapplication.model.MatchVoteItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;



public class MakeMatch2Activity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MakeMatch2ActivityTAG";
    Button makematchBtn;
    public static ArrayList<Activity> matchActivityList = new ArrayList<Activity>();

    EditText editTextPrice, editTextVsClubName, editTextMaxPeople;
    String matchId,section, matchDate, voteDueDate, place, address, price, vsClubName, maxPeople, dinner;
    DatabaseReference dbMatchInfo,dateDF,clubDF,dbMatchVote,dbUsers;

    Button btnYes,btnNo,srchClubBtn;

    String currentMatchKey,myClubKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_match2);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        myClubKey = preferences.getString("myClubKey", null);
//        matchActivityList.add(this);
        editTextPrice = findViewById(R.id.price);
        editTextVsClubName = findViewById(R.id.vsClubName);
        editTextMaxPeople = findViewById(R.id.maxPeople);

        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);
        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);

        srchClubBtn = findViewById(R.id.srchClubBtn);
        srchClubBtn.setOnClickListener(this);

        Intent getIntent = getIntent();
        section = getIntent.getStringExtra("section");
        matchDate = getIntent.getStringExtra("matchDate");
        voteDueDate = getIntent.getStringExtra("voteDueDate");
        place = getIntent.getStringExtra("place");
        address = getIntent.getStringExtra("address");


        Frag1 frag1 = new Frag1();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction t = manager.beginTransaction();
        MatchInfoItem mathInfoItem = new MatchInfoItem();
        MatchDateItem matchDateItem = new MatchDateItem();
        dbMatchInfo = FirebaseDatabase.getInstance().getReference().child("MatchInfo");
        dateDF= FirebaseDatabase.getInstance().getReference().child("DateInfo");
        clubDF = FirebaseDatabase.getInstance().getReference().child("ClubInfo");
        dbMatchVote = FirebaseDatabase.getInstance().getReference().child("MatchVote");
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        makematchBtn = (Button) findViewById(R.id.makematchBtn);
        makematchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < matchActivityList.size(); i++) {
                    matchActivityList.get(i).finish();
                }
                price = editTextPrice.getText().toString().trim();
                vsClubName = editTextVsClubName.getText().toString().trim();
                maxPeople = editTextMaxPeople.getText().toString().trim();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                clubDF.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot :snapshot.getChildren()){
                            Log.d(TAG,"클럽id:"+dataSnapshot.getKey());
                            mathInfoItem.setSection(section);
                            mathInfoItem.setMatchDate(matchDate);
                            mathInfoItem.setVoteDueDate(voteDueDate);
                            mathInfoItem.setPlace(place);
                            mathInfoItem.setAddress(address);
                            mathInfoItem.setPrice(price);
                            mathInfoItem.setVsClubName(vsClubName);
                            mathInfoItem.setMaxPeople(maxPeople);
                            mathInfoItem.setDinner(dinner);
                            mathInfoItem.setClubId(dataSnapshot.getKey());
                            mathInfoItem.setVoteTrue(0);
                            mathInfoItem.setVoteFalse(0);
                            dbMatchInfo.push().setValue(mathInfoItem);
                            setMatchVoteTable();
                            break;
                        }
                        Intent intent = new Intent(MakeMatch2Activity.this, MatchInfoActivity.class);
                        intent.putExtra("matchInfoData",  mathInfoItem);
                        intent.putExtra("create",1010);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


//                dateDF.push().setValue(matchDateItem);
                Toast.makeText(MakeMatch2Activity.this, "매치생성 완료!", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnYes:
                btnYes.setBackgroundColor(Color.parseColor("#155453"));
                btnYes.setTextColor(Color.parseColor("#FBEFE9"));
                btnNo.setBackground(getResources().getDrawable(R.drawable.btnstyle1,null));
                btnNo.setTextColor(Color.parseColor("#155453"));
                dinner = "yes";
                break;
            case R.id.btnNo:
                btnNo.setBackgroundColor(Color.parseColor("#155453"));
                btnNo.setTextColor(Color.parseColor("#FBEFE9"));
                btnYes.setBackground(getResources().getDrawable(R.drawable.btnstyle1,null));
                btnYes.setTextColor(Color.parseColor("#155453"));
                dinner = "no";
                break;
            case R.id.srchClubBtn:
                startActivity(new Intent(getApplicationContext(),SearchClubActivity.class));
                break;
        }
    }

    private void setMatchVoteTable(){
        /*
        1. 지금 이 매치에
        2. 클럽 모든 멤버를 추가한다.
        3. voteFalse = false, voteTrue = false 를
         */
        Log.d(TAG,"메소드 들어옴");
        dbMatchInfo.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Log.d(TAG,"안"+dataSnapshot.getValue());
                    dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot userData : snapshot.getChildren()){
                                dbUsers.child(userData.getKey()).child("myclub").addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        Log.d(TAG,"MYCLUB: "+snapshot.getValue());
                                        String userJoinedClubKey = (String) snapshot.getValue();
                                        MatchVoteItem matchVoteItem = new MatchVoteItem();
                                        matchVoteItem.setVoteFalse(false);
                                        matchVoteItem.setVoteTrue(false);
                                        Log.d(TAG,"MYCLUBKEY: "+myClubKey);
                                        if(userJoinedClubKey.equals(myClubKey)){
                                            dbMatchVote.child(dataSnapshot.getKey()).child(userData.getKey()).setValue(matchVoteItem);
                                        }

                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d(TAG,"1"+error.toString());
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(TAG,"2"+error.toString());
                        }
                    });


                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG,"3"+error.toString());
            }
        });
    }
}