package com.example.goputapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.zip.Inflater;

public class PopupMemberActivity extends Activity {

    TextView txtText;
    ListView listview;

    LinearLayout memberlistLinearLayout;
    ListMemberViewAdapter adapter;
    ScrollView votememberscrollview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //커스텀 다이얼로그에 원하지 않는 검은 색 배경색이 보일때 처리 방법
        getWindow().setBackgroundDrawable(new PaintDrawable(Color.TRANSPARENT));
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_member);

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.txtText);

        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        txtText.setText(data);


        listview = (ListView) findViewById(R.id.memberlistview);
        // Adapter 생성
        adapter = new ListMemberViewAdapter() ;
        // 리스트뷰 참조 및 Adapter달기
        listview.setAdapter(adapter);
        //리스트뷰 줄없애기
        listview.setDivider(null);

        for(int i=1;i<=6;i++){
            adapter.addItem(Integer.toString(i),"최종혁", ContextCompat.getDrawable(this, R.drawable.soccerplayer)) ;
        }
//        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//        LinearLayout linearLayout = (LinearLayout) inflater.inflate( R.layout.memberlistview, null );
//
//        memberlistLinearLayout = (LinearLayout)linearLayout.findViewById(R.id.memberlistLinearLayout);
//        votememberscrollview = (ScrollView)findViewById(R.id.votememberscrollview);
//        votememberscrollview.setMinimumHeight(6*linearLayout.getMeasuredHeight());
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
//        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
//        Intent intentback= new Intent(this, MatchInfo.class);
//        startActivityForResult(intentback, 1);
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