package com.example.goputapplication;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goputapplication.Adapter.MainNoticeAdapter;
import com.example.goputapplication.model.ClubNameCount;
import com.example.goputapplication.model.MainNoticeItem;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.math.MathUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.customcalendar.CustomCalendar;
import org.customcalendar.OnDateSelectedListener;
import org.customcalendar.OnNavigationButtonClickedListener;
import org.customcalendar.Property;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.goputapplication.MatchInfoActivity.actList;


public class InfoAcitivity extends AppCompatActivity implements OnNavigationButtonClickedListener {

    private static final String TAG = "InfoACTIVITYTAG";
    private static final String MY_DB = "my_db";
    private static final int ACCESSCODE = 3030;

    //??????
    private ImageButton notificationBtn;
    //?????????
    private View mProgressView;
    private View info_form;
    private TextView tvLoad, todayMatchDate, todayMatchPlace, todayMatchAddress;

    //?????? ????????????, ???????????? ?????????
    private TableLayout matchExist;
    private TextView matchNotExist;
    private String currentDate,currentFullDate, setCurrentClubKey;

    //?????? ??????
    private ArrayList<MainNoticeItem> noticeList;
    private MainNoticeAdapter mainNoticeAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView mainNoticeMoreBtn;

    private LinearLayout progressLinearLayout;
    private ImageView goPutLogo;
    //??????
    CustomCalendar customCalendar;
    HashMap<Integer, Object> dateHashMap;
    Calendar calendar;
    ValueEventListener valueEventListener;

    ArrayList<String> dayList = new ArrayList<>();
    MatchDateManager matchDateManager = new MatchDateManager();

    int currentYear, currentMonth, currentDayOfMonth;
    //    ListView listview;
//    ListViewAdapter adapter;
    Context context;

    //Toolbar ???????????? ??????
    ImageButton backbtn;
    // ??????????????? ???????????? ????????? ????????? ?????? ??????
    private long backKeyPressedTime = 0;
    // ??? ?????? ???????????? ????????? ????????? ??????
    private Toast toastback;
    //Bottom Navigation ??????
    BottomAppBar bottomAppBar;
    //Floating Button -> ?????? ????????????
    FloatingActionButton addmatchfloatingbutton;
    ExtendedFloatingActionButton joinClubFloatBtn, makeMatchFloatBtn;

    private Animation rotateOpen, rotateClose, fromBottom, toBottom;

    Boolean clicked = false;


    //BottomSheetList
    FrameLayout scrim;
    BottomSheetBehavior bottomSheetBehavior;
    LinearLayout mBottemSheet;
    ListBottomSheetViewAdapter bottomSheetadapter;
    ListView bottomSheetlistview;
    ConstraintLayout thisweeklayout;
    int bottomSheetFlag = 0;
    Boolean addButtonFlag = false;

    //FireBase ??????
    private FirebaseDatabase database;
    private DatabaseReference dbMatchInfo, dbClubInfo, dbrfUser, dbMyClub, dbMainNotice, dbMember;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    String myClubKey;
    //??????????????????
    private TextView currentClubName;
    String currentClubKey;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //progress bar
        info_form = (View) findViewById(R.id.info_form);
        mProgressView = (View) findViewById(R.id.login_progress);
        tvLoad = (TextView) findViewById(R.id.tvLoad);
        progressLinearLayout = (LinearLayout) findViewById(R.id.progressLinearLayout);

        todayMatchDate = (TextView) findViewById(R.id.todayMatchDate);
        todayMatchPlace = (TextView) findViewById(R.id.todayMatchPlace);
        todayMatchAddress = (TextView) findViewById(R.id.todayMatchAddress);
        matchExist = (TableLayout) findViewById(R.id.matchExist);
        matchNotExist =  (TextView) findViewById(R.id.matchNotExist);

        //?????? ?????? Adapter ?????????
       recyclerView = (RecyclerView) findViewById(R.id.mainNotice);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        noticeList = new ArrayList<>();
        mainNoticeAdapter = new MainNoticeAdapter(noticeList);
        recyclerView.setAdapter(mainNoticeAdapter);
        mainNoticeMoreBtn = (TextView)findViewById(R.id.mainNoticeMoreBtn);
        mainNoticeMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainNoticeAcitivity.class));
            }
        });

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        //????????????
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        //????????? ????????? ?????????????????? ??????
        database = FirebaseDatabase.getInstance();
        dbMatchInfo = database.getReference("MatchInfo");
        dbClubInfo = database.getReference("ClubInfo");
//        checkClubAndUser();
        dbMember = database.getReference("Member");
        dbrfUser = database.getReference("Users");

        //?????? ????????????
        currentClubName = (TextView) findViewById(R.id.currentClubName);
        goPutLogo = (ImageView) findViewById(R.id.goPutLogo);
        showProgress(true);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentClubKey = preferences.getString("currentClubKey", null);
        //??????????????? ????????? myClubKey
        myClubKey = preferences.getString("myClubKey", null);
        Log.d(TAG, " currentClubKey:" + currentClubKey);
        Log.d(TAG, " myClubKey:" + myClubKey);
        //MakeClubActivity?????? ?????? myClubKey
        Intent makeClubIntent = getIntent();
        if (myClubKey == null) {
            myClubKey = makeClubIntent.getStringExtra("myClubKey");
        }

        if (currentClubKey != null) {
            setCurrentClubKey = currentClubKey;
        } else {
            setCurrentClubKey = myClubKey;
        }
        init(setCurrentClubKey);

        //System.out.println("??????:"+  myAccount.getAccount().getNickName());
        //?????? activity ?????? ?????????????????????
        actList.add(this);
        this.context = this;
        //????????? ??? ?????? ??????
        SharedPreferences sp = getSharedPreferences(MY_DB, Context.MODE_PRIVATE);
        boolean hasVisited = sp.getBoolean("hasVisited", false);
        if (!hasVisited) {
            //??? ??????????????? popup?????????
            Intent intent = new Intent(this, PopupWelcomeActivity.class);

            dbrfUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        GoPutUser goPutUser = snapshot1.getValue(GoPutUser.class);
                        if (firebaseUser.getUid().equals(goPutUser.registName)) {
                            intent.putExtra("nickname", goPutUser.registName);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            startActivity(intent);

            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("hasVisited", true);
            e.commit();
        }
        //??????
        notificationBtn = (ImageButton) findViewById(R.id.notificationBtn);
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoAcitivity.this, NotificationActivity.class));
            }
        });


//        Calendar cal = Calendar.getInstance(new Locale("en", "UK")); // ????????? ?????? ?????? ?????????
//        cal.set(2021,4,18);
//        System.out.println("????????????: "+cal);

//        Date date = new Date();

        // create WeekFields
        WeekFields weekFields = WeekFields.of(DayOfWeek.SUNDAY, 1);

        // apply weekOfMonth()
        TemporalField weekOfMonth = weekFields.weekOfMonth();

        // create a LocalDate
        LocalDate day = LocalDate.of(2021, 3, 28);

        // get week of month for localdate
        int wom = day.get(weekOfMonth);

        // print results
        System.out.println("week of month for " + day + " :" + wom);

        joinClubFloatBtn = (ExtendedFloatingActionButton) findViewById(R.id.joinClubFloatBtn);
        makeMatchFloatBtn = (ExtendedFloatingActionButton) findViewById(R.id.makeMatchFloatBtn);


        // Adapter ??????
//        adapter = new ListViewAdapter();

        // ???????????? ?????? ??? Adapter??????
//        listview = (ListView) findViewById(R.id.matchlistview);
//        listview.setAdapter(adapter);
        //???????????? ????????????
//        listview.setDivider(null);


//        dbMatchInfo = database.getReference("MatchInfoTest");
//        dbMatchInfo.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //?????????????????? ????????????????????? ???????????? ???????????????
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    ListViewItem item = snapshot.getValue(ListViewItem.class);
////                    adapter.addItem(item.getWeek(), item.getSection(), item.getPlace(), item.getDate(), item.getState());
//                }
////                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        //?????? ?????? ??????
        TextView currenttime;
        long mNow = System.currentTimeMillis();
        Date mReDate = new Date(mNow);
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy.MM.dd [EE]");
        currentFullDate = fullDateFormat.format(mReDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.M.d");
        currentDate = dateFormat.format(mReDate);
        currenttime = (TextView) findViewById(R.id.currenttime);
        currenttime.setText(currentFullDate);

//        SimpleDateFormat getMonth = new SimpleDateFormat("M???");
//        String formatMonth = getMonth.format(mReDate);
//        matchinfoMonth = (TextView) findViewById(R.id.matchinfoMonth);
//        matchinfoMonth.setText(formatMonth);

        bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mypage:
                    startActivity(new Intent(this, MyPageActivity.class));
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                    return true;
                case R.id.home:
                    Toast.makeText(context, "HOME", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.viewallmember:
                    Intent intent = new Intent(this, ShowAllMember.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                default:
                    return false;
            }
        });
        //Bottom Navigation ??????
//        thisweeklayout = (ConstraintLayout) findViewById(R.id.thisweeklayout);

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });

        //?????? ?????? ?????? ?????????
        addmatchfloatingbutton = (FloatingActionButton) findViewById(R.id.addmatchfloatingbutton);
        addmatchfloatingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent makematchIntent = new Intent(v.getContext(), MakeMatchActivity.class);
//                startActivity(makematchIntent);
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                floatingButtonCliked();
            }
        });

        makeMatchFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makematchIntent = new Intent(v.getContext(), MakeMatchActivity.class);
                startActivity(makematchIntent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                floatingButtonCliked();
            }
        });
        joinClubFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent joinMatchIntent = new Intent(v.getContext(), SearchClubActivity.class);
                joinMatchIntent.putExtra("ACCESSCODE", ACCESSCODE);
                startActivity(joinMatchIntent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                floatingButtonCliked();
            }
        });
        // ????????? ????????? listview??? ?????? ????????? ????????? ??????.
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView parent, View v, int position, long id) {
//                // get item
//                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);
//
//                String week = item.getWeek();
//                String section = item.getSection();
//                String place = item.getPlace();
//                String date = item.getDate();
//                String state = item.getState();
////
//
//                //activity_Info xml?????? _??? ?????? ????????????
////                TextView matchinfoMonth = (TextView)findViewById(R.id.matchinfoMonth);
//                //bottomsheet??? ????????? TextView ????????????
//                TextView monthweek = (TextView) mBottemSheet.findViewById(R.id.monthweek);
//                //??????
////                monthweek.setText(matchinfoMonth.getText() + " " + week);
//                // TODO : use item data.
//                showBottomSheet();
//
//            }
//        });

        bottomSheetadapter = new ListBottomSheetViewAdapter();
        bottomSheetlistview = (ListView) findViewById(R.id.myclublistview);
        bottomSheetlistview.setAdapter(bottomSheetadapter);
        bottomSheetlistview.setDivider(null);


//        navigationView = (NavigationView)findViewById(R.id.navigationView);
//        bottomSheetBehavior = BottomSheetBehavior.from(navigationView);

        mBottemSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        TextView myClubName = (TextView) mBottemSheet.findViewById(R.id.myClubName);
        TextView myClubMemberCount = (TextView) mBottemSheet.findViewById(R.id.myClubMemberCount);
        LinearLayout myClubBtn = (LinearLayout) mBottemSheet.findViewById(R.id.myClubBtn);

        //?????? ????????? setText????????? ???
        //1. ?????? ????????? ?????? ?????? 2. ????????? ?????? ??????(????????????????????????)
        dbClubInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ClubInfo clubInfo = dataSnapshot.getValue(ClubInfo.class);
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (clubInfo.id.equals(firebaseUser.getUid())) {
                        myClubName.setText(clubInfo.clubName);
                        dbMember.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                    if (dataSnapshot.getKey().equals(dataSnapshot1.getKey())) {
                                        myClubMemberCount.setText(String.valueOf(dataSnapshot1.getChildrenCount()));
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

        //?????? ????????? ????????? ID ????????????
        dbMyClub = database.getReference("Users/" + firebaseUser.getUid() + "/myclub");
        //??????????????? ?????? ?????? ????????? ??????????????? ???????????? addValueEventLIstener??????
        dbMyClub.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bottomSheetadapter.listBottomSheetViewItemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String myClubIdJoined = String.valueOf(dataSnapshot.getValue());
//                    Log.d(TAG, "??????????????????:" + myClubIdJoined);
                    //???????????? ????????????
                    dbClubInfo = database.getReference("ClubInfo");
                    dbClubInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String clubName;
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
//                                Log.d(TAG, "????????????:" + dataSnapshot1.getKey());
                                ClubInfo clubInfo = dataSnapshot1.getValue(ClubInfo.class);
                                if (dataSnapshot1.getKey().equals(myClubIdJoined) && dataSnapshot1.getKey().equals(myClubKey) == false) {
//                                    Log.d(TAG, "????????????:" + clubInfo.getClubName());
                                    clubName = clubInfo.getClubName();
                                    //?????? ????????? ????????? ??????

                                    dbMember.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String count = null;
                                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
//                                                Log.d(TAG,"??????id:"+dataSnapshot1.getKey());
                                                if (myClubIdJoined.equals(dataSnapshot1.getKey())) {
                                                    //????????? ?????? ????????? ?????????????????? list??? ??????????????????
//                                                    Log.d(TAG, dataSnapshot1.getKey() + ":??? ?????? ????????? ??????id");
//                                                    Log.d(TAG, dataSnapshot1.getChildrenCount() + ":?????????");
                                                    count = String.valueOf(dataSnapshot1.getChildrenCount());
                                                    break;
                                                }
                                            }
                                            Log.d(TAG, "????????????[ ????????????:" + clubName + ", ?????????:" + count);
                                            bottomSheetadapter.addItem(clubName, count);
                                            bottomSheetadapter.notifyDataSetChanged();
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        bottomSheetBehavior = BottomSheetBehavior.from(mBottemSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

//        scrim = (FrameLayout) findViewById(R.id.scrim);
//        scrim.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideBottomSheet();
//            }
//        });

        //????????? ????????? ??? ??????????????? ??????
        myClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("currentClubKey", myClubKey);
                edit.commit();
                finish();
                startActivity(getIntent());
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        bottomSheetlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListBottomSheetViewItem item = (ListBottomSheetViewItem) parent.getItemAtPosition(position);

                String count = item.getCount();
                String clubName = item.getClubName();
                Toast.makeText(context, "????????????:" + clubName, Toast.LENGTH_SHORT).show();
                // TODO : use item data.
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                dbClubInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ClubInfo clubInfo = dataSnapshot.getValue(ClubInfo.class);
                            if (clubInfo.getClubName().equals(clubName)) {
                                edit.putString("currentClubKey", dataSnapshot.getKey());
                                Log.d(TAG, "????????? ??????ID" + dataSnapshot.getKey());
                                edit.apply();

                                finish();
                                startActivity(getIntent());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float baseAlpha = ResourcesCompat.getFloat(getResources(), R.dimen.material_emphasis_medium);
                float offset = (slideOffset - (-1f)) / (1f - (-1f)) * (1f - 0f) + 0f;
                int alpha = (int) MathUtils.lerp(0f, 255f, offset * baseAlpha);
                if (alpha < offset) {
                    hideBottomSheet();
                }
            }

        });


        //??????
        customCalendar = findViewById(R.id.custom_calendar);
        HashMap<Object, Property> descHashMap = new HashMap<>();

        Property matchProperty = new Property();
        matchProperty.layoutResource = R.layout.matchdate_view;
        matchProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("match", matchProperty);

        Property currentProperty = new Property();
        currentProperty.layoutResource = R.layout.current_view;
//        currentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("current", currentProperty);

        customCalendar.setMapDescToProp(descHashMap);
        dateHashMap = new HashMap<>();

        calendar = Calendar.getInstance();

        SimpleDateFormat getMonth = new SimpleDateFormat("MM");
        currentMonth = Integer.parseInt(getMonth.format(mReDate));

        SimpleDateFormat getYear = new SimpleDateFormat("yyyy");
        currentYear = Integer.parseInt(getYear.format(mReDate));
//        Log.d(TAG, "???" + currentYear + "???" + currentMonth);


        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        dateHashMap.put(currentDayOfMonth, "current");
        customCalendar.setDate(calendar, dateHashMap);


        //???????????? ???????????? ?????? ???????????? ????????? ?????? ????????? ??????????????? ??????
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this);
        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
//                String setCurrentClubKey;
//                if (currentClubKey != null) {
//                    setCurrentClubKey = currentClubKey;
//                } else {
//                    setCurrentClubKey = myClubKey;
//                }
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MatchInfoItem matchInfoItem = dataSnapshot.getValue(MatchInfoItem.class);

                            Log.d(TAG, "TEST:" + matchInfoItem.getMatchDate());
                            String[] value = matchInfoItem.getMatchDate().split("\\.");
                            if (Integer.parseInt(value[2]) == selectedDate.get(Calendar.DAY_OF_MONTH)) {
//                                Log.d(TAG,"??????");
                                Intent intent = new Intent(getApplicationContext(), MatchInfoActivity.class);
                                Log.d(TAG, "MATCHKEY:" + dataSnapshot.getKey());
                                intent.putExtra("matchKey", dataSnapshot.getKey());
                                intent.putExtra("info", 1011);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                dbMatchInfo.orderByChild("clubId").equalTo(setCurrentClubKey).addListenerForSingleValueEvent(valueEventListener);

            }
        });


    }

    public void init(String currentKey) {
        showMatchDate(currentKey);
        setClubName(currentKey);
        setTodayMatch();
        setNoticeList();
        showProgress(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar??? back??? ????????? ??? ??????
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // ?????? ???????????? ????????? ????????? ???????????? ???????????? ?????? ??????
        // super.onBackPressed();

        // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ?????? ??????????????? ?????? ???
        // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ???????????? Toast Show
        // 2000 milliseconds = 2 seconds


        if (bottomSheetFlag == 1) {
            hideBottomSheet();
            return;
        } else if (addButtonFlag == true) {
            floatingButtonCliked();
            return;
        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                toastback = Toast.makeText(this, "\'??????\' ????????? ?????? ??? ???????????? ???????????????.", Toast.LENGTH_SHORT);
                toastback.show();
                return;
            }
            // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ?????? ??????????????? ?????? ???
            // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ????????? ???????????? ??????
            // ?????? ????????? Toast ??????
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                for (int i = 0; i < actList.size(); i++) actList.get(i).finish();
                finish();
                //?????? ?????? ?????? ?????? ??????
//                moveTaskToBack(true);						// ???????????? ?????????????????? ??????
//                finishAndRemoveTask();						// ???????????? ?????? + ????????? ??????????????? ?????????
//                android.os.Process.killProcess(android.os.Process.myPid());	// ??? ???????????? ??????
                toastback.cancel();
            }
        }
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        return;
    }

    public void showBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        addmatchfloatingbutton.hide();
        bottomSheetFlag = 1;

//        scrim.setBackgroundColor(Color.parseColor("#BDB8B8"));
//        thisweeklayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    public void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        addmatchfloatingbutton.show();
        bottomSheetFlag = 0;

//        scrim.setBackgroundColor(Color.parseColor("#F2F2F2"));
//        thisweeklayout.setBackground(ContextCompat.getDrawable(this, R.drawable.box2));
    }

    @Override
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
        Map<Integer, Object>[] arr = new Map[2];
        arr[0] = new HashMap<>();
        arr[1] = null;
        Log.d(TAG, "??????" + String.valueOf(newMonth.get(Calendar.MONTH)));
        //?????? ?????? ??????
        if (currentYear == newMonth.get(Calendar.YEAR) && newMonth.get(Calendar.MONTH) + 1 == currentMonth) {
            arr[0].put(currentDayOfMonth, "current");
        }

        //????????? ?????? ??????
        if (matchDateManager.getMatchDate() != null) {
            ArrayList<String> dateList = matchDateManager.getMatchDate();
            Log.d(TAG, String.valueOf(newMonth.get(Calendar.YEAR)));
            for (int i = 0; i < dateList.size(); i++) {
                String[] values = dateList.get(i).split("\\.");
                int year = Integer.parseInt(values[0]);
                int month = Integer.parseInt(values[1]);
                int day = Integer.parseInt(values[2]);
                if (newMonth.get(Calendar.YEAR) == year) {
                    if (newMonth.get(Calendar.MONTH) == month - 1) {
                        arr[0].put(day, "match");
                    }
                }
            }
            return arr;
        } else {
            return new Map[0];
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            //??????????????? ?????? ?????????
            goPutLogo.setVisibility(show ? View.VISIBLE : View.GONE);
            goPutLogo.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    goPutLogo.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });


            currentClubName.setVisibility(show ? View.GONE : View.VISIBLE);
            currentClubName.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    currentClubName.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            info_form.setVisibility(show ? View.GONE : View.VISIBLE);
            info_form.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    info_form.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            progressLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            progressLinearLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            info_form.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void showMatchDate(String currentClubKey) {
        Intent intent = getIntent();
        int ACCESSCODE = intent.getIntExtra("ACCESSCODE", 0);
        String clubIdFromSrch = intent.getStringExtra("clubId");

        if (ACCESSCODE == 2020) {
            //???????????? ????????? clubid??? ?????? matchinfo?????? ????????? ???????????? dayList??? ????????????
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MatchInfoItem matchInfoItem = dataSnapshot.getValue(MatchInfoItem.class);
                        dayList.add(matchInfoItem.getMatchDate());
                    }
                    calendarDateSetting();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            dbMatchInfo.orderByChild("clubId").equalTo(clubIdFromSrch).addValueEventListener(valueEventListener);
        } else {
            Log.d(TAG, "????????? ??????ID" + currentClubKey);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dayList.clear();

                    if (currentClubKey != null) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //?????? ?????? ????????? ???????????? ?????? MatchInfoItem?????? ??????
                            MatchInfoItem mathInfoItem = snapshot.getValue(MatchInfoItem.class);
                            String clubId = mathInfoItem.getClubId();
                            if (clubId != null && clubId.equals(currentClubKey)) {
                                String date = mathInfoItem.getMatchDate();
                                dayList.add(date);
                            }
                        }
                    } else {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //?????? ?????? ????????? ???????????? ?????? MatchInfoItem?????? ??????
                            MatchInfoItem mathInfoItem = snapshot.getValue(MatchInfoItem.class);
                            String clubId = mathInfoItem.getClubId();
                            if (clubId != null && clubId.equals(myClubKey)) {
                                String date = mathInfoItem.getMatchDate();
                                dayList.add(date);
                            }
                        }
                    }
                    calendarDateSetting();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            dbMatchInfo.addValueEventListener(valueEventListener);
        }
    }

    //????????? ?????? ????????? ???????????? dateList??? set ????????? ?????????
    public void calendarDateSetting() {
        matchDateManager.setMatchDate(dayList);
        dateHashMap.clear();
        ArrayList<String> dateList = matchDateManager.getMatchDate();
        dateHashMap.put(currentDayOfMonth, "current");
        for (int i = 0; i < dateList.size(); i++) {
            String[] values = dateList.get(i).split("\\.");
            int year = Integer.parseInt(values[0]);
            int month = Integer.parseInt(values[1]);
            int day = Integer.parseInt(values[2]);
            if (currentYear == year && month == currentMonth) {
                dateHashMap.put(day, "match");
                customCalendar.setDate(calendar, dateHashMap);
            }
        }
//        showProgress(false);
    }


    public void setClubName(String currentClubKey) {
        //????????? ?????? ????????? ????????????----------------------------------------------
        dbClubInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "????????? ?????? ???????????? ????????? ?????????");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d(TAG, "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                    ClubInfo clubInfoData = dataSnapshot.getValue(ClubInfo.class);
                    Log.d(TAG, dataSnapshot.getKey());
                    if (dataSnapshot.getKey().equals(currentClubKey)) {
                        Log.d(TAG, "????????? ?????????????????? ??????");
                        currentClubName.setText("\"" + clubInfoData.clubName + "\"");
                        break;
                    } else if (currentClubKey == null && dataSnapshot.getKey().equals(myClubKey)) {
                        Log.d(TAG, "??? ?????????????????? ??????");
                        currentClubName.setText("\"" + clubInfoData.clubName + "\"");
//                        showProgress(false);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void floatingButtonCliked() {
        setVisibility(clicked);
        setAnimation(clicked);
        clicked = !clicked;
        addButtonFlag = !addButtonFlag;
    }

    private void setVisibility(Boolean clicked) {
        if (!clicked) {
            joinClubFloatBtn.setVisibility(View.VISIBLE);
            makeMatchFloatBtn.setVisibility(View.VISIBLE);
        } else {
            joinClubFloatBtn.setVisibility(View.INVISIBLE);
            makeMatchFloatBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(Boolean clicked) {
        if (!clicked) {
            joinClubFloatBtn.startAnimation(fromBottom);
            makeMatchFloatBtn.startAnimation(fromBottom);
            addmatchfloatingbutton.startAnimation(rotateOpen);
        } else {
            joinClubFloatBtn.startAnimation(toBottom);
            makeMatchFloatBtn.startAnimation(toBottom);
            addmatchfloatingbutton.startAnimation(rotateClose);
        }
    }

    private void setTodayMatch(){
        //????????? ?????? : ????????????, ????????????key

        dbMatchInfo.orderByChild("clubId").equalTo(setCurrentClubKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean chk = false;
                for(DataSnapshot ddsMatchInfo : snapshot.getChildren()){
                    MatchInfoItem item = ddsMatchInfo.getValue(MatchInfoItem.class);
                    //?????? ?????????
                    if(item.getMatchDate().equals(currentDate)){
                        String[] values = currentDate.split("\\.");
                        int year = Integer.parseInt(values[0]);
                        int month = Integer.parseInt(values[1]);
                        int day = Integer.parseInt(values[2]);
                        matchExist.setVisibility(View.VISIBLE);
                        matchNotExist.setVisibility(View.GONE);
                        todayMatchDate.setText(year+"??? "+month+"??? "+day+"???");
                        todayMatchPlace.setText(item.getPlace());
                        todayMatchAddress.setText(item.getAddress());
                        chk = true;
                        break;
                    }
                }
                //?????? ?????????
                if(chk==false){
                    matchExist.setVisibility(View.GONE);
                    matchNotExist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void setNoticeList(){
        dbMainNotice = database.getReference("MainNotice");
        dbMainNotice.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noticeList.clear();
                for(DataSnapshot dssMainNotice : snapshot.getChildren()){
                    MainNoticeItem item = dssMainNotice.getValue(MainNoticeItem.class);
                    mainNoticeAdapter.addItem(item.getCategory(),item.getTitle());
                }
                mainNoticeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}