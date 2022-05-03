package com.example.goputapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class ShowAllMember extends Activity {

    ListView listview;
    ListAllMemberViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //커스텀 다이얼로그에 원하지 않는 검은 색 배경색이 보일때 처리 방법
        getWindow().setBackgroundDrawable(new PaintDrawable(Color.TRANSPARENT));
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_all_member);

        listview = (ListView) findViewById(R.id.allmemberlistview);
        // Adapter 생성
        adapter = new ListAllMemberViewAdapter();
        // 리스트뷰 참조 및 Adapter달기
        listview.setAdapter(adapter);
        //리스트뷰 줄없애기
        listview.setDivider(null);

        adapter.addItem("최종혁", "25세", ContextCompat.getDrawable(this, R.drawable.soccerplayer),
                "최형준", "25세", ContextCompat.getDrawable(this, R.drawable.soccerplayer));

        adapter.addItem("최종혁", "25세", ContextCompat.getDrawable(this, R.drawable.soccerplayer),
                "최형준", "25세", ContextCompat.getDrawable(this, R.drawable.soccerplayer));

        adapter.addItem("최종혁", "25세", ContextCompat.getDrawable(this, R.drawable.soccerplayer),
                "최형준", "25세", ContextCompat.getDrawable(this, R.drawable.soccerplayer));


    }
    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
//        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}