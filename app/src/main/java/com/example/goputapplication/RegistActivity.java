package com.example.goputapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.goputapplication.RegistFinsihActivity.registList;

public class RegistActivity extends AppCompatActivity {

    Button nextbtn;

    EditText registId,registPw,registPwCheck,registPhoneNum;
    String rgId,rgPw,rgPwChk,rgPhoneNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        registId = findViewById(R.id.registId);
        registPw = findViewById(R.id.registPw);
        registPwCheck = findViewById(R.id.registPwCheck);
        registPhoneNum = findViewById(R.id.registPhoneNum);
        registPhoneNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


        nextbtn = (Button)findViewById(R.id.nextbtn);
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser(){
        rgId = registId.getText().toString().trim();
        rgPw = registPw.getText().toString().trim();
        rgPwChk = registPwCheck.getText().toString().trim();
        rgPhoneNum = registPhoneNum.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(rgId).matches()){
            registId.setText("확인 : "+rgId);
            registId.setError("이메일 형식을 확인해주세요!");
            registId.requestFocus();
            return;
        }
        if(rgId.isEmpty()){
            registId.setError("이메일을 입력해주세요!");
            registId.requestFocus();
            return;
        }
        if(rgPw.isEmpty()){
            registPw.setError("패스워드를 입력해주세요!");
            registPw.requestFocus();
            return;
        }
        if(rgPwChk.isEmpty()){
            registPwCheck.setError("패스워드 확인을 입력해주세요!");
            registPwCheck.requestFocus();
            return;
        }
        if(rgPw.length() <8 && rgPw.length() >12 ){
            registPw.setError("패스워드는 8~12자리 입니다.");
            registPw.requestFocus();
            return;
        }

        String pwPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{8,12}$";
        Matcher matcher = Pattern.compile(pwPattern).matcher(rgPw);

        pwPattern = "(.)\\1\\1\\1";
        Matcher matcher2 = Pattern.compile(pwPattern).matcher(rgPw);

        if(!matcher.matches()){
            registPw.setError("영문 대소문자, 숫자, 특수문자를 확인하여 다시 입력해주세요!");
            registPw.requestFocus();
            return;
        }

        if(matcher2.find()){
            registPw.setError("같은 문자가 4개 이상 존재합니다!");
            registPw.requestFocus();
            return;
        }

        if(rgPw.contains(rgId)){
            registPw.setError("비밀번호에 아이디가 존재합니다!");
            registPw.requestFocus();
            return;
        }

        if(rgPw.contains(" ")){
            registPw.setError("스페이스바는 안됩니다!");
            registPw.requestFocus();
            return;
        }


        if(rgPw.equals(rgPwChk) == false){
            registPwCheck.setError("패스워드가 같지 않습니다.");
            registPwCheck.requestFocus();
            return;
        }
        if(rgPhoneNum.isEmpty()){
            registPhoneNum.setError("전화번호를 입력해주세요!");
            registPhoneNum.requestFocus();
            return;
        }

        registList.add(RegistActivity.this);
        Intent intent = new Intent(getApplicationContext(), RegistActivity2.class);
        intent.putExtra("id",rgId);
        intent.putExtra("password",rgPw);
        intent.putExtra("passwordcheck",rgPwChk);
        intent.putExtra("phonenum",rgPhoneNum);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}