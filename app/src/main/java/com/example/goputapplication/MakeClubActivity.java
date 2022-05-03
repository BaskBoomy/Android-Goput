package com.example.goputapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.goputapplication.LoginActivity.clubInfo;
import static com.example.goputapplication.MatchInfoActivity.actList;

public class MakeClubActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MakeClubActivityTAG";
    private Button makeClubBtn,btnSoccer,btnPutSal;
    private EditText editTextClubName, editTextMajor, editTextAgeGroup, editTextAbility;
    private String clubId, clubName, major, ageGroup, ability;
    private DatabaseReference dbClubInfo, dbUsers, dbMember;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseUser user;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;


    private Spinner ageGroupSpinner,abilitySpinner;
    private String[] ageGroupItems,abilityItems;
    String getAgeGroup,getAbility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeclub);
        actList.add(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        edit = sharedPreferences.edit();
        //Firebase Instance
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        dbUsers = database.getReference("Users/" + mAuth.getCurrentUser().getUid() + "/myclub");
        dbMember = database.getReference("Member");

        editTextClubName = findViewById(R.id.clubName);

        btnSoccer = findViewById(R.id.btnSoccer);
        btnPutSal = findViewById(R.id.btnPutSal);
        btnSoccer.setOnClickListener(this);
        btnPutSal.setOnClickListener(this);
//        editTextMajor = findViewById(R.id.major);
//        editTextAgeGroup = findViewById(R.id.ageGroup);
//        editTextAbility = findViewById(R.id.ability);

        ageGroupSpinner = findViewById(R.id.ageGroupSpinner);
        abilitySpinner = findViewById(R.id.abilitySpinner);
        ageGroupItems = getResources().getStringArray(R.array.age_group);
        abilityItems = getResources().getStringArray(R.array.ability);

        ageGroupSpinner.setAdapter(setSpinnerText(ageGroupItems));
        abilitySpinner.setAdapter(setSpinnerText(abilityItems));
        ageGroupSpinner.setSelection(0);
        abilitySpinner.setSelection(0);

        //dialog으로 설정시 제목으로 필요
//        ageGroupSpinner.setPrompt("연령대 선택");
//        ageGroupSpinner.setPrompt("능력치 선택");
//        ageGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        ClubInfo clubInfoManager = new ClubInfo();
        dbClubInfo = database.getReference().child("ClubInfo");

        makeClubBtn = (Button) findViewById(R.id.makeClubBtn);
        makeClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clubName = editTextClubName.getText().toString().trim();
//                major = editTextMajor.getText().toString().trim();
                ageGroup = ageGroupSpinner.getSelectedItem().toString();
                ability = abilitySpinner.getSelectedItem().toString();
                clubInfoManager.setClubName(clubName);
                clubInfoManager.setMajor(major);
                clubInfoManager.setAgeGroup(ageGroup);
                clubInfoManager.setAbility(ability);
                clubInfoManager.setId(mAuth.getCurrentUser().getUid());
                dbClubInfo.push().setValue(clubInfoManager);


//                setClubIdToMyClubTable();


                dbClubInfo.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Log.d(TAG,dataSnapshot.getKey());
                            clubId = dataSnapshot.getKey();
                            Intent intent = new Intent(v.getContext(), InfoAcitivity.class);
                            clubInfo.setClubInfo(clubId, user.getUid(), clubName, major, ageGroup, ability);
                            dbUsers.push().setValue(clubId);
                            dbMember.child(clubId).push().setValue(user.getUid());
                            Toast.makeText(MakeClubActivity.this, "클럽생성 완료!", Toast.LENGTH_SHORT).show();
                            intent.putExtra("myClubKey",clubId);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//                            edit.putString("myClubKey", clubId);
//                            edit.putString("myClubUid", user.getUid());
//                            edit.putString("myClubName", clubName);
//                            edit.putString("myClubMajor", major);
//                            edit.putString("myClubAgeGroup", ageGroup);
//                            edit.putString("myClubAbility", ability);
//                            edit.commit();
                            break;
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });







            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSoccer:
               chooseSoccer();
                break;
            case R.id.btnPutSal:
                choosePutSal();
                break;
        }
    }
    private void setClubIdToMyClubTable() {
        dbClubInfo.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Log.d(TAG,dataSnapshot.getKey());
                    clubId = dataSnapshot.getKey();
                    edit.putString("myClubKey", clubId);
                    edit.putString("myClubUid", user.getUid());
                    edit.putString("myClubName", clubName);
                    edit.putString("myClubMajor", major);
                    edit.putString("myClubAgeGroup", ageGroup);
                    edit.putString("myClubAbility", ability);
                    edit.commit();
                    break;
                }

                clubInfo.setClubInfo(clubId, user.getUid(), clubName, major, ageGroup, ability);
                dbUsers.push().setValue(clubId);
                dbMember.child(clubId).push().setValue(user.getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        reff.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    clubId = dataSnapshot.getKey();
//                    edit.putString("myClubKey", clubId);
//                    edit.apply();
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    clubInfo.setClubInfo(clubId, user.getUid(), clubName, major, ageGroup, ability);
//                    dbUsers.push().setValue(clubId);
//                    dbMember.child(clubId).push().setValue(user.getUid());
//                    break;
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
    public void chooseSoccer(){
        btnSoccer.setBackgroundColor(Color.parseColor("#155453"));
        btnSoccer.setTextColor(Color.parseColor("#FBEFE9"));
        btnPutSal.setBackground(getResources().getDrawable(R.drawable.btnstyle1,null));
        btnPutSal.setTextColor(Color.parseColor("#155453"));
        major = "축구";
    }
    public void choosePutSal(){
        btnPutSal.setBackgroundColor(Color.parseColor("#155453"));
        btnPutSal.setTextColor(Color.parseColor("#FBEFE9"));
        btnSoccer.setBackground(getResources().getDrawable(R.drawable.btnstyle1,null));
        btnSoccer.setTextColor(Color.parseColor("#155453"));
        major = "풋살";
    }
    public SpinnerAdapter setSpinnerText(String[] itemArray) {
        SpinnerAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.spinner_item, itemArray) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Typeface externalFont = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    externalFont = getResources().getFont(R.font.nanumsquare_acb);
                }
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(12);
                ((TextView) v).setTextColor(Color.parseColor("#155453"));
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                Typeface externalFont = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    externalFont = getResources().getFont(R.font.nanumsquare_acr);
                }
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(12);
                ((TextView) v).setTextColor(Color.parseColor("#FBEFE9"));
                v.setBackgroundColor(Color.parseColor("#155453"));
                return v;
            }
        };
        return spinnerAdapter;
    }
}