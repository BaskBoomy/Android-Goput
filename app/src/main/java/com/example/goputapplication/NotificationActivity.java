package com.example.goputapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationActivity extends AppCompatActivity implements OnRefreshViewListner {

    private static final String TAG = "NotificationActivityTAG";
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;

    ListView clubInviteNoti;
    ListNotfiInviteViewAdapter adapter;
    SwipeRefreshLayout pullToRefresh;

    //로딩창
    private LinearLayout Notification_view;
    private LinearLayout progressLinearLayout;
    SharedPreferences preferences;
    SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        edit = preferences.edit();
        edit.putBoolean("notificationAddedFlag",false);
        edit.apply();

        //progress bar
        Notification_view = (LinearLayout) findViewById(R.id.Notification_view);
        progressLinearLayout = (LinearLayout) findViewById(R.id.progressBar_Notification);

        showProgress(true);

        pullToRefresh = findViewById(R.id.pullToRefresh);
        clubInviteNoti = (ListView) findViewById(R.id.clubInviteNoti);
        clubInviteNoti.setDivider(null);
        adapter = new ListNotfiInviteViewAdapter(this);
        clubInviteNoti.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        getNotificaionData();
        showProgress(false);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                printNotificationData();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void refreshView() {
        getNotificaionData();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            Notification_view.setVisibility(show ? View.GONE : View.VISIBLE);
            Notification_view.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Notification_view.setVisibility(show ? View.GONE : View.VISIBLE);
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
            Notification_view.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void getNotificaionData(){
        databaseReference = database.getReference("Notification");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG,"onChildAdded");
                Boolean flag = preferences.getBoolean("notificationAddedFlag",false);
                Log.d(TAG,"flag : "+flag);
                //1. 데이터 추가될때 한번만 작동되면되는데...
                printNotificationData();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG,"onChildChanged");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //지워진 데이터가 들어옴!!
                Log.d(TAG,"onChildRemoved");
                edit.putBoolean("notificationAddedFlag",false);
                edit.apply();
                printNotificationData();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG,"onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG,"onCancelled");
            }
        });

    }

    private void printNotificationData(){
        Boolean flag = preferences.getBoolean("notificationAddedFlag",false);
        Log.d(TAG,"flag2 : "+flag);
        if(flag==false) {
            adapter.ListNotiInviteViewItemList.clear();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ListNotiInviteViewItem item = snapshot.getValue(ListNotiInviteViewItem.class);
                        if (item.getReceiverId().equals(firebaseUser.getUid())) {
                            adapter.addItem(item.getProfileImg(), item.getName(), item.getSenderId(), item.getReceiverId(), item.getMainText());
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            edit.putBoolean("notificationAddedFlag", true);
            edit.apply();
        }
    }
}
