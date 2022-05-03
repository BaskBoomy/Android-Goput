package com.example.goputapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.goputapplication.model.MatchVoteItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.app.Activity.RESULT_OK;
import static com.example.goputapplication.MatchInfoActivity.noticeItemManager;

public class Frag2 extends Fragment implements View.OnClickListener {
    private final String TAG = "Frag2";

    TextView showvotemember, voteTrueCount, voteFalseCount;
    ConstraintLayout voteTrue, voteFalse;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseUser fUser;
    DatabaseReference dbMatchInfo, dbMatchVote,dbUsers;
    SharedPreferences preferences;
    Context context;
    String matchKey;
    ImageView trueCheckImg, falseCheckImg;
    @Nullable
    @Override
    public ViewGroup onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_vote, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());


        showvotemember = (TextView) view.findViewById(R.id.showvotemember);
        showvotemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PopupMemberActivity.class);
                startActivity(intent);
            }
        });

        voteTrue = (ConstraintLayout) view.findViewById(R.id.voteTrue);
        voteFalse = (ConstraintLayout) view.findViewById(R.id.voteFalse);
        voteTrueCount = (TextView) view.findViewById(R.id.voteTrueCount);
        voteFalseCount = (TextView) view.findViewById(R.id.voteFalseCount);

        trueCheckImg = (ImageView) view.findViewById(R.id.trueCheckImg);
        falseCheckImg = (ImageView) view.findViewById(R.id.falseCheckImg);

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        dbUsers= database.getReference("Users");
        //매치 처음 만들었을때

        //매치 만들고 나서 InfoActivity에서 들어올때
        if(noticeItemManager.getNotice().getACCESSCODE()== 1011){
            matchKey = preferences.getString("matchKey", null);
            dbMatchInfo = database.getReference("MatchInfo").child(matchKey);
            dbMatchVote = database.getReference("MatchVote").child(matchKey).child(fUser.getUid());
            setVoteCount();
            voteTrue.setOnClickListener(this);
            voteFalse.setOnClickListener(this);
        }



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 참가 불참 버튼 누르면 activity가 새로 생김
        // 해결 : 그냥 기존의 activity를 종료시켜버리기로함 -> 안됨.
//        getActivity().finish();
//        getActivity().overridePendingTransition(R.anim.fadeinfast, R.anim.fadeoutfast );
//        Log.d(TAG,"없어짐");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.voteTrue:
                updateVote(R.id.voteTrue);
                break;
            case R.id.voteFalse:
                updateVote(R.id.voteFalse);
                break;
        }
    }

    private void updateVote(int id) {
        dbMatchVote.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, snapshot.getValue().toString());
                MatchVoteItem item = snapshot.getValue(MatchVoteItem.class);
                Log.d(TAG, item.toString());
                //참가 첫 투표
                if (item.getVoteTrue() == false && item.getVoteFalse() == false) {
                    if (id == R.id.voteTrue) setVoteTrue();
                    else if (id == R.id.voteFalse) setVoteFalse();
                }
                //1. 재투표할때 2. 똑같은거 눌렀을때
                else if (item.getVoteTrue() == false && item.getVoteFalse() == true) {
                    if (id == R.id.voteTrue)  setVoteTrue();
                    else if (id == R.id.voteFalse) Toast.makeText(context, "이미 \"불참\"에 투표하셨습니다!", Toast.LENGTH_SHORT).show();
                }
                else if (item.getVoteTrue() == true && item.getVoteFalse() == false) {
                    if (id == R.id.voteTrue)  Toast.makeText(context, "이미 \"참가\"에 투표하셨습니다!", Toast.LENGTH_SHORT).show();
                    else if (id == R.id.voteFalse) setVoteFalse();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setVoteTrue() {
        dbMatchInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MatchInfoItem item = snapshot.getValue(MatchInfoItem.class);
                int trueCount = item.getVoteTrue();
                dbMatchInfo.child("voteTrue").setValue(trueCount+1);


                dbMatchVote.child("voteFalse").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int falseCount = item.getVoteFalse();
                        if(snapshot.getValue().equals(false)&&falseCount > 0){
                            falseCount--;
                            dbMatchInfo.child("voteFalse").setValue(falseCount);;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dbMatchVote.child("voteTrue").setValue(true);
        dbMatchVote.child("voteFalse").setValue(false);
        Toast.makeText(context, "투표완료!", Toast.LENGTH_SHORT).show();
    }

    private void setVoteFalse() {
        dbMatchInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MatchInfoItem item = snapshot.getValue(MatchInfoItem.class);
                int falseCount = item.getVoteFalse();
                dbMatchInfo.child("voteFalse").setValue(falseCount+1);

                dbMatchVote.child("voteTrue").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int trueCount = item.getVoteTrue();
                        if(snapshot.getValue().equals(false)&&trueCount > 0){
                            trueCount--;
                            dbMatchInfo.child("voteTrue").setValue(trueCount);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dbMatchVote.child("voteFalse").setValue(true);
        dbMatchVote.child("voteTrue").setValue(false);
        Toast.makeText(context, "투표완료!", Toast.LENGTH_SHORT).show();
    }

    private void setVoteCount() {
        dbMatchInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MatchInfoItem item = snapshot.getValue(MatchInfoItem.class);
                int trueCount = item.getVoteTrue();
                int falseCount = item.getVoteFalse();
                voteTrueCount.setText(trueCount + "명");
                voteFalseCount.setText(falseCount + "명");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dbMatchVote.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MatchVoteItem item = snapshot.getValue(MatchVoteItem.class);
                if(item.getVoteFalse()==false&&item.getVoteTrue()==true){
                    trueCheckImg.setVisibility(View.VISIBLE);
                    falseCheckImg.setVisibility(View.GONE);
                }else if(item.getVoteFalse()==true&&item.getVoteTrue()==false){
                    trueCheckImg.setVisibility(View.GONE);
                    falseCheckImg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
