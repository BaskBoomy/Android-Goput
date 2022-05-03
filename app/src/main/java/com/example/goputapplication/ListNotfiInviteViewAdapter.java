package com.example.goputapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.goputapplication.model.MatchVoteItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListNotfiInviteViewAdapter extends BaseAdapter {

    private static final String TAG = "ListNotfiInviteViewAdapter";
    private DatabaseReference dbMember, dbUsers, dbNotification,dbMatchInfo,dbMatchVote;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    NotificationActivity notificationActivity = new NotificationActivity();
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    public ArrayList<ListNotiInviteViewItem> ListNotiInviteViewItemList = new ArrayList<ListNotiInviteViewItem>();
    private OnRefreshViewListner mRefreshListner;
    String sender_Id, receiver_Id, myClubKey;
    // ListViewAdapter의 생성자
    public ListNotfiInviteViewAdapter(Context context) {
        mRefreshListner = (OnRefreshViewListner) context;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return ListNotiInviteViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notiinvitelistview, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView profileImg = (ImageView) convertView.findViewById(R.id.profileImg);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView senderId = (TextView) convertView.findViewById(R.id.senderId);
        TextView receiverId = (TextView) convertView.findViewById(R.id.receiverId);
        Button accept = (Button) convertView.findViewById(R.id.accept);
        Button deny = (Button) convertView.findViewById(R.id.deny);

        TextView mainText = (TextView) convertView.findViewById(R.id.mainText);

        // Data Set(ListMemberViewItemList)에서 position에 위치한 데이터 참조 획득
        ListNotiInviteViewItem listNotiInviteViewItem = ListNotiInviteViewItemList.get(position);

        sender_Id = listNotiInviteViewItem.getSenderId();
        receiver_Id = listNotiInviteViewItem.getReceiverId();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(convertView.getContext());
        myClubKey = preferences.getString("myClubKey", null);
        String myClubName = preferences.getString("myClubName", null);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        dbMember = database.getReference("Member");
        dbUsers = database.getReference("Users/" + sender_Id + "/myclub");
        dbMatchInfo =  database.getReference("MatchInfo");
        dbMatchVote =  database.getReference("MatchVote");

        dbNotification = database.getReference("Notification");
        // 아이템 내 각 위젯에 데이터 반영
        Glide.with(context).load(listNotiInviteViewItem.getProfileImg()).into(profileImg);
        name.setText(listNotiInviteViewItem.getName());
        senderId.setText(listNotiInviteViewItem.getSenderId());
        receiverId.setText(listNotiInviteViewItem.getReceiverId());
        mainText.setText(listNotiInviteViewItem.getMainText());
        if (listNotiInviteViewItem.getProfileImg() == null) {
            accept.setVisibility(View.GONE);
            deny.setText("확인");
            deny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //알림 삭제
                    dbNotification.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ListNotiInviteViewItem item = dataSnapshot.getValue(ListNotiInviteViewItem.class);
                                if (item.getReceiverId().equals(receiver_Id) && item.getSenderId().equals(sender_Id)) {
                                    dataSnapshot.getRef().removeValue();

                                    if (position != 0) ListNotiInviteViewItemList.remove(position);
                                    break;
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        } else {
            deny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //알림 삭제
                    dbNotification.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ListNotiInviteViewItem item = dataSnapshot.getValue(ListNotiInviteViewItem.class);
                                if (item.getReceiverId().equals(receiver_Id) && item.getSenderId().equals(sender_Id)) {
                                    dataSnapshot.getRef().removeValue();

                                    if (position != 0) ListNotiInviteViewItemList.remove(position);
                                    break;
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //거절 알림 보내기
                    ListNotiInviteViewItem denyMsg = new ListNotiInviteViewItem();
                    denyMsg.setProfileImg(null);
                    denyMsg.setSenderId(receiver_Id);
                    denyMsg.setReceiverId(sender_Id);
                    denyMsg.setName(myClubName);
                    denyMsg.setMainText("리더가 거절하였습니다.");
                    dbNotification.push().setValue(denyMsg);
                    mRefreshListner.refreshView();
                }
            });
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1. Member테이블에 insert
                    dbMember.child(myClubKey).push().setValue(sender_Id);
                    //2. Users테이블의 myclub에 insert
                    dbUsers.push().setValue(myClubKey);
                    //문제점 : Notification 테이블이 비어있으면 갑자기 여러개 컬럼이 추가되는 오류가 발생함
                    //왜 JoinClubDialog으로 가서 알림을 보내는지 모르겠음..!!!!!
                    //해결 : "JoinClubDialog"에서 105line : addValueEventListener가 아니라 addListenerForSingleValueEvent로 수정..
                    //3. 해당 Notification 삭제
                    dbNotification.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ListNotiInviteViewItem item = dataSnapshot.getValue(ListNotiInviteViewItem.class);
                                if (item.getReceiverId().equals(receiver_Id) && item.getSenderId().equals(sender_Id)) {
                                    dataSnapshot.getRef().removeValue();

                                    if (position != 0) ListNotiInviteViewItemList.remove(position);
                                    break;
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //5. 투표 멤버 추가 MatchVote
                    //새로들어온 멤버들을 투표테이블에 추가하도록 만들기
                    MatchVoteItem matchVoteItem = new MatchVoteItem();
                    matchVoteItem.setVoteFalse(false);
                    matchVoteItem.setVoteTrue(false);
                    dbMatchInfo.orderByChild("clubId").equalTo(myClubKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dssMatchInfo : snapshot.getChildren()){
                                dbMatchVote.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dssMatchVote : snapshot.getChildren()){
                                            String matchKey = dssMatchVote.getKey();
                                            if(dssMatchInfo.getKey().equals(dssMatchVote.getKey())){
                                                dbMatchVote.child(matchKey).child(sender_Id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.getValue() == null)
                                                            dbMatchVote.child(matchKey).child(sender_Id).setValue(matchVoteItem);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
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

                    //6. 새로 고침
                    notifyDataSetChanged();
                    mRefreshListner.refreshView();
                }
            });
        }
        return convertView;
    }


    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return ListNotiInviteViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String profileImg, String name, String senderId, String receiverId, String mainText) {
        ListNotiInviteViewItem item = new ListNotiInviteViewItem();

        item.setProfileImg(profileImg);
        item.setName(name);
        item.setSenderId(senderId);
        item.setReceiverId(receiverId);
        item.setMainText(mainText);
        ListNotiInviteViewItemList.add(item);
    }

    public void setMyClub() {

    }
}