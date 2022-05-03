package com.example.goputapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class ListVoteViewAdapter extends BaseAdapter {

    String popupdata="팝업데이터 미설정";
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListVoteViewItem> ListVoteViewItemList = new ArrayList<ListVoteViewItem>() ;

    // ListViewAdapter의 생성자
    public ListVoteViewAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return ListVoteViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.votelistview, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView datevote = (TextView) convertView.findViewById(R.id.datevote) ;
        TextView countvote = (TextView) convertView.findViewById(R.id.countvote) ;
        Button btnmember = (Button) convertView.findViewById(R.id.memberBtn);
        // Data Set(ListVoteViewItemList)에서 position에 위치한 데이터 참조 획득
        ListVoteViewItem listVoteViewItem = ListVoteViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        datevote.setText(listVoteViewItem.getDatevote());
        countvote.setText(listVoteViewItem.getCountvote());
        btnmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PopupMemberActivity.class);
                intent.putExtra("data", listVoteViewItem.getPopupdata());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return ListVoteViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String datevote, String countvote,Button btnmember,String popupdata) {
        ListVoteViewItem item = new ListVoteViewItem();

        item.setDatevote(datevote);
        item.setCountvote(countvote);
        item.setMember(btnmember,popupdata);
        ListVoteViewItemList.add(item);
    }
}