package com.example.goputapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.math.MathUtils;
import com.google.android.material.navigation.NavigationView;

public class BottomNavigation extends AppCompatActivity {

    NavigationView navigationView;
    BottomAppBar bottomAppBar;
    FrameLayout scrim;
    BottomSheetBehavior bottomSheetBehavior;

    LinearLayout mBottemSheet;
    ListBottomSheetViewAdapter bottomSheetadapter;
    ListView bottomSheetlistview;

    @Override
    public void onBackPressed() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        bottomSheetadapter = new ListBottomSheetViewAdapter();
        bottomSheetlistview = (ListView) findViewById(R.id.myclublistview);
        bottomSheetlistview.setAdapter(bottomSheetadapter);
        bottomSheetlistview.setDivider(null);

        // 아이템 추가.
        bottomSheetadapter.addItem("3월 31일 월", "4명");
        bottomSheetadapter.addItem("5월 31일 금", "6명");
        bottomSheetadapter.addItem("4월 31일 월", "4명");
        bottomSheetadapter.addItem("3월 28일 화", "12명");
//        navigationView = (NavigationView)findViewById(R.id.navigationView);
//        bottomSheetBehavior = BottomSheetBehavior.from(navigationView);

        mBottemSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(mBottemSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

//        navigationView.setNavigationItemSelectedListener( menuItem -> {
//            // Handle menu item selected
//            menuItem.isChecked();
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//            return true;
//        });

        scrim = (FrameLayout) findViewById(R.id.scrim);
        scrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//                scrim.setBackgroundColor(Color.parseColor("#BDB8B8"));
            }

        });


    }
}