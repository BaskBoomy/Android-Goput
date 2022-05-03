package com.example.goputapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAllMemberViewAdapter extends BaseAdapter {

    String popupdata="팝업데이터 미설정";
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListAllMemberViewItem> ListAllMemberViewItemList = new ArrayList<ListAllMemberViewItem>() ;

    // ListViewAdapter의 생성자
    public ListAllMemberViewAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return ListAllMemberViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.allmemberlistview, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView membername1 = (TextView) convertView.findViewById(R.id.membername1) ;
        TextView memberage1 = (TextView) convertView.findViewById(R.id.memberage1) ;
        ImageView memberimg1 = (ImageView) convertView.findViewById(R.id.memberimg1);

        TextView membername2 = (TextView) convertView.findViewById(R.id.membername2) ;
        TextView memberage2 = (TextView) convertView.findViewById(R.id.memberage2) ;
        ImageView memberimg2 = (ImageView) convertView.findViewById(R.id.memberimg2);
        // Data Set(ListMemberViewItemList)에서 position에 위치한 데이터 참조 획득
        ListAllMemberViewItem listAllMemberViewItem = ListAllMemberViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        membername1.setText(listAllMemberViewItem.getName1());
        memberage1.setText(listAllMemberViewItem.getAge1());
        memberimg1.setImageDrawable(listAllMemberViewItem.getImg1());

        membername2.setText(listAllMemberViewItem.getName2());
        memberage2.setText(listAllMemberViewItem.getAge2());
        memberimg2.setImageDrawable(listAllMemberViewItem.getImg2());

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
        return ListAllMemberViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String membername1, String memberage1,Drawable memberimg1,String membername2, String memberage2,Drawable memberimg2) {
        ListAllMemberViewItem item = new ListAllMemberViewItem();

        item.setName1(membername1);
        item.setAge1(memberage1);
        item.setImg1(memberimg1);

        item.setName2(membername2);
        item.setAge2(memberage2);
        item.setImg2(memberimg2);
        ListAllMemberViewItemList.add(item);
    }
}