package com.example.goputapplication;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.goputapplication.LoginActivity.clubInfo;


public class MatchInfoActivity extends FragmentActivity {
    private static final String TAG = "MATCHINFOTAG";
    private static final Boolean DATAINPUT_SUCCESS = false;

    ImageButton backToHome;
    private final int FROMCREATE = 1010, FROMINFO = 1011, FROMSEARCH = 1012;
    public static ArrayList<Activity> actList = new ArrayList<Activity>();

    FloatingActionButton addmatchfloatingbutton;
    BottomAppBar bottomAppBar;

    private NotificationManagerCompat notificationManager;

    TextView textViewSection, textViewMatchDate, textViewPlace, textViewAddress, textViewMaxPeople;
    TextView participateCount;

    public static NoticeItemManager noticeItemManager = new NoticeItemManager();

    public static NoticeItem noticeItemFromCreate;

    private FirebaseDatabase database;
    private DatabaseReference dbMatchInfo;
    String matchKey;
    SectionsPagerAdapter sectionsPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_info);
        //클럽키 받아오기
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String currentClubKey = preferences.getString("currentClubKey",null);
        String myClubKey = preferences.getString("myClubKey",null);
        //매치키 받아오기
        Intent getMatchInfoData = getIntent();
        matchKey = getMatchInfoData.getExtras().getString("matchKey");
        Log.d(TAG,"MATCHKEY:"+matchKey);
        sectionsPagerAdapter = new SectionsPagerAdapter(getApplicationContext(), getSupportFragmentManager());

        database = FirebaseDatabase.getInstance();
        dbMatchInfo = database.getReference().child("MatchInfo");

        participateCount = (TextView)findViewById(R.id.participateCount);



        backToHome = (ImageButton) findViewById(R.id.backbtn);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < actList.size(); i++) actList.get(i).finish();
                Intent intent = new Intent(v.getContext(), InfoAcitivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });

        addmatchfloatingbutton = (FloatingActionButton) findViewById(R.id.addmatchfloatingbutton);
        addmatchfloatingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makematchIntent = new Intent(v.getContext(), MakeMatchActivity.class);
                startActivity(makematchIntent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mypage:
                    Toast.makeText(this, "MYPAGE 버튼 눌림", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.home:
                    Intent intent = new Intent(this, InfoAcitivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    return true;
                case R.id.viewallmember:
                    Intent viewMemberIntent = new Intent(this, ShowAllMember.class);
                    startActivity(viewMemberIntent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                default:
                    return false;
            }
        });

        //알림 보내기 기능
        ImageButton alertvote;
        alertvote = (ImageButton) findViewById(R.id.alertvote);

        notificationManager = NotificationManagerCompat.from(this);


//        alertvote.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendOnChannel1(v);
//            }
//        });

        textViewSection = findViewById(R.id.section);
        textViewMatchDate = findViewById(R.id.matchDate);
        textViewPlace = findViewById(R.id.place);
        textViewAddress = findViewById(R.id.address);
        textViewMaxPeople = findViewById(R.id.maxPeople);




        //매치 만들어졌을때 받아올 데이터들
        if (getMatchInfoData.getIntExtra("create", 0) == FROMCREATE) {
            Log.d(TAG,"FROM : MakeMatch2Activity");
            noticeItemFromCreate = new NoticeItem();
            MatchInfoItem matchInfoItem = (MatchInfoItem) getMatchInfoData.getSerializableExtra("matchInfoData");
            textViewSection.setText(matchInfoItem.getSection());
            textViewMatchDate.setText(matchInfoItem.getMatchDate());
            textViewPlace.setText(matchInfoItem.getPlace());
            textViewAddress.setText(matchInfoItem.getAddress());
            textViewMaxPeople.setText(matchInfoItem.getMaxPeople());

            noticeItemFromCreate.setPrice(matchInfoItem.getPrice());
            noticeItemFromCreate.setMaxPeople(matchInfoItem.getMaxPeople());



            setFragmentAdapter();
        } else if (getMatchInfoData.getIntExtra("info", 0) == FROMINFO) {
            Bundle bundle = new Bundle();
            bundle.putInt("ACCESSCODE",FROMINFO);
            Frag2 frag2 = new Frag2();
            frag2.setArguments(bundle);

            setParticipateMemberCount();
            Log.d(TAG,"FROM : InfoActivity");
            String setCurrentClubKey;
            if(currentClubKey!=null){
                setCurrentClubKey = currentClubKey;
            }else{
                setCurrentClubKey = myClubKey;
            }
            Log.d(TAG,"setCurrentClubKey :"+setCurrentClubKey);
            dbMatchInfo.orderByChild("clubId").equalTo(setCurrentClubKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MatchInfoItem matchInfoItem = dataSnapshot.getValue(MatchInfoItem.class);

                        if (dataSnapshot.getKey().equals(matchKey)) {
                            Log.d(TAG, "들어옴");
                            textViewSection.setText(matchInfoItem.getSection());
                            textViewMatchDate.setText(matchInfoItem.getMatchDate());
                            textViewPlace.setText(matchInfoItem.getPlace());
                            textViewAddress.setText(matchInfoItem.getAddress());
                            textViewMaxPeople.setText(matchInfoItem.getMaxPeople());
                            Log.d(TAG, "getPrice : " + matchInfoItem.getPrice() + "getMaxPeople : " + matchInfoItem.getMaxPeople());
                            //Fragment로 객체 데이터 보내기

                            noticeItemManager.setNotice(matchInfoItem.getPrice(), matchInfoItem.getMaxPeople(), FROMINFO);
                            SharedPreferences.Editor edit = preferences.edit();
                            edit.putString("matchKey",matchKey);
                            edit.apply();

                            setFragmentAdapter();


                            break;
                        }
                    }
                    Log.d(TAG, "밖성공:" + noticeItemManager.getNotice().getACCESSCODE());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
//
    }
//    public void sendOnChannel1(View v) {
//        Notification notification = new NotificationCompat.Builder(this, App.CHANNER_1_ID)
//                .setSmallIcon(R.drawable.soccerplayer)
//                .setContentTitle("GoPut")
//                .setContentText("투표를 아직 안했습니다!")
//                .setDefaults(Notification.DEFAULT_VIBRATE)
//                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .build();
//
//        notificationManager.notify(1, notification);
//    }

    public void setFragmentAdapter() {
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private void setParticipateMemberCount(){
        dbMatchInfo.child(matchKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MatchInfoItem item = snapshot.getValue(MatchInfoItem.class);
                int ppMember = item.getVoteTrue();
                if(ppMember>0){
                    participateCount.setText(String.valueOf(item.getVoteTrue()));
                }else{
                    participateCount.setText(String.valueOf("0"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}