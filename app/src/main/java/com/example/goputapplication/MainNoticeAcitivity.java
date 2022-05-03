package com.example.goputapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.goputapplication.Adapter.MainNoticeAdapter;
import com.example.goputapplication.Adapter.MainNoticeAdapter2;
import com.example.goputapplication.model.MainNoticeItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainNoticeAcitivity extends AppCompatActivity {
    //메인 공지
    private ArrayList<MainNoticeItem> noticeList;
    private MainNoticeAdapter2 mainNoticeAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    //FireBase 연동
    private FirebaseDatabase database;
    private DatabaseReference dbMainNotice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_notice_acitivity);

        database = FirebaseDatabase.getInstance();
        //메인 공지 Adapter 연결
        recyclerView = (RecyclerView) findViewById(R.id.mainNotice2);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        noticeList = new ArrayList<>();
        mainNoticeAdapter = new MainNoticeAdapter2(noticeList);
        recyclerView.setAdapter(mainNoticeAdapter);

        setNoticeList();
    }

    public void setNoticeList(){
        dbMainNotice = database.getReference("MainNotice");
        dbMainNotice.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noticeList.clear();
                for(DataSnapshot dssMainNotice : snapshot.getChildren()){
                    MainNoticeItem item = dssMainNotice.getValue(MainNoticeItem.class);
                    mainNoticeAdapter.addItem(item.getCategory(),item.getTitle(), item.getContent());
                }
                mainNoticeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}