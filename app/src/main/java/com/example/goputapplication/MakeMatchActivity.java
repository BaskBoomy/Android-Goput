package com.example.goputapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import static com.example.goputapplication.MakeMatch2Activity.matchActivityList;

public class MakeMatchActivity extends AppCompatActivity implements View.OnClickListener {

    Button makematchnextBtn;
    EditText section, matchDate, voteDueDate, place, address;
    Button setMatchDate,setVoteDate;

    String strMatchDate,strVoteDueDate;
    int DateCode =0;

    DialogFragment newFragment = new DatePickerFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_match);
        matchActivityList.add(this);

        section = findViewById(R.id.section);
        matchDate = findViewById(R.id.matchDate);
        voteDueDate = findViewById(R.id.voteDueDate);
        place = findViewById(R.id.place);
        address = findViewById(R.id.address);
        setMatchDate = findViewById(R.id.setMatchDate);
        setVoteDate = findViewById(R.id.setVoteDate);
        setMatchDate.setOnClickListener(this);
        setVoteDate.setOnClickListener(this);


//        actList.add(this);
        makematchnextBtn = (Button) findViewById(R.id.makematchnextBtn);
        makematchnextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MakeMatch2Activity.class);
                intent.putExtra("section",section.getText().toString().trim());
                intent.putExtra("matchDate",strMatchDate);
                intent.putExtra("voteDueDate",strVoteDueDate);
                intent.putExtra("place",place.getText().toString().trim());
                intent.putExtra("address",address.getText().toString().trim());
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }
    public void showDatePicker(View view) {

    }
    public void setMatchDate(int year, int month, int day){
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        String dateMessage = (year_string + "."+ month_string + "." + day_string);
        String showMessage = (year_string + " 년 "+ month_string + " 월 " + day_string+ " 일");

        if(DateCode == 5873){
            strMatchDate = dateMessage;
            matchDate.setText(showMessage);
            matchDate.setVisibility(View.VISIBLE);
        }
        if(DateCode == 5874){
            strVoteDueDate = dateMessage;
            voteDueDate.setText(showMessage);
            voteDueDate.setVisibility(View.VISIBLE);
        }
//        Toast.makeText(this,"Date: "+dateMessage,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setMatchDate:
                DateCode=5873;
                newFragment.show(getSupportFragmentManager(),"datePicker");
                break;
            case R.id.setVoteDate :
                DateCode=5874;
                newFragment.show(getSupportFragmentManager(),"datePicker");
                break;
        }
    }
}
