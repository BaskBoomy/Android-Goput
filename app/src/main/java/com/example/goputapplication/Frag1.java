package com.example.goputapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.goputapplication.MatchInfoActivity.noticeItemFromCreate;
import static com.example.goputapplication.MatchInfoActivity.noticeItemManager;
public class Frag1 extends Fragment implements View.OnClickListener {

    private final String TAG = "Frag1";
    Button tokakao;
    TextView textViewPerPerson, textViewPrice;
    NoticeItem noticeItem;

    int perprice;

//    NoticeItemManager noticeItemManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//        Intent i = getActivity().getIntent();
//        noticeItemManager = (NoticeItemManager) i.getSerializableExtra("noticeItemManager");
        MatchInfoActivity matchInfoActivity = new MatchInfoActivity();

        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_main, container, false);
        textViewPerPerson = (TextView) view.findViewById(R.id.perPerson);
        textViewPrice = (TextView) view.findViewById(R.id.fragPrice);
        Log.d(TAG, String.valueOf(noticeItemManager.getNotice().getACCESSCODE()));
        if(noticeItemManager.getNotice().getACCESSCODE()== 1011){
            noticeItem = noticeItemManager.getNotice();
        }else{
            noticeItem = noticeItemFromCreate;
        }
        textViewPrice.setText(noticeItem.getPrice()+"원");
        perprice = Integer.parseInt(noticeItem.getPrice()) / Integer.parseInt(noticeItem.getMaxPeople());
        textViewPerPerson.setText(Integer.toString(perprice)+"원");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tokakao = (Button) view.findViewById(R.id.tokakao);
        tokakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Button clicked", Toast.LENGTH_LONG).show();
                Intent intent = getContext().getPackageManager().getLaunchIntentForPackage("com.kakao.talk");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {


    }
}
