package com.example.goputapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import static com.example.goputapplication.LoginRegistActivity.finishFromLogin;
import static com.example.goputapplication.RegistFinsihActivity.registList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    //progressbar 설정
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    private static final String TAG = "LOGINACTIVITYTAG";
    private View kakaoLogin, kakaoLogout;

    private TextView kakaonickname;
    private ImageView kakaoprofileimg;

    public static KaKaoAccountManager myAccount = new KaKaoAccountManager();
    public static ClubInfoManager clubInfo = new ClubInfoManager();

    //FireBase 연동
    private FirebaseDatabase database;
    private DatabaseReference databaseReference, dbff,dbUsers;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private FirebaseFunctions mFunctions;
    private RequestQueue mRequestQueue;
    Task<AuthResult> kakaoLoginResult;

    private EditText loginEmail, loginPassWord;
    private Button signIn;

    //로그인 여부

    //비밀번호 찾기
    TextView findPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //로그인
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassWord = (EditText) findViewById(R.id.loginPassWord);

        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        //Firebase Instance
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        mFunctions = FirebaseFunctions.getInstance();

        //비밀번호 찾기
        findPassword = findViewById(R.id.findPassword);
        findPassword.setOnClickListener(this);

        //progress bar
        mLoginFormView = (View) findViewById(R.id.login_form);
        mProgressView = (View) findViewById(R.id.login_progress);
        tvLoad = (TextView) findViewById(R.id.tvLoad);

//        updateKakaoLoginUi();
        kakaoLogin = findViewById(R.id.kakaoLogin);

        //파이어 베이스 데이터베이스 연동
        database = FirebaseDatabase.getInstance();
        dbff = database.getReference("ClubInfo");

        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                //oAuthToken이 전달이 되면 로그인 성공
                if (oAuthToken != null) {
                    String accessToken = oAuthToken.getAccessToken();
                    Log.d(TAG, oAuthToken.getAccessToken());
                    kakaoLoginResult = getFirebaseJwt(accessToken).continueWithTask(new Continuation<String, Task<AuthResult>>() {
                        @Override
                        public Task<AuthResult> then(@NonNull Task<String> task) throws Exception {
                            String firebaseToken = task.getResult();
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            return auth.signInWithCustomToken(firebaseToken);
                        }
                    }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to create a Firebase user.", Toast.LENGTH_LONG).show();
                                if (task.getException() != null) {
                                    Log.e(TAG, task.getException().toString());
                                }
                            }
                        }
                    });
//                    checkClubAndUser();
                }

                kakaoLoginResult.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //카카오 정보가 있으면 그대로 로그인
                            dbUsers = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.getValue() != null){
                                        Toast.makeText(getApplicationContext(), "카카오톡으로 로그인 성공!", Toast.LENGTH_LONG).show();
                                        SetClubInfo();
                                        checkClubAndUser();
                                    }else{
                                        //카카오 첫 로그인시 FirebaseAuth에 등록
                                        updateKakaoLogin();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "카카오톡으로 로그인 실패", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                //오류 여부
                if (throwable != null) {

                }
                return null;
            }
        };
        kakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                //카카오 설치 여부에 따라..
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    //카카오 어플을 통해서 로그인
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                } else {
                    //카카오 홈페이지를 통해서 로그인
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
                }
            }
        });



    }


    private void updateKakaoLogin() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) {
                    GoPutUser goPutUser = new GoPutUser(
                            user.getKakaoAccount().getProfile().getNickname()
                            , String.valueOf(user.getKakaoAccount().getAgeRange())
                            , null
                            , user.getKakaoAccount().getEmail());
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(goPutUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "카카오 정보 등록 완료!", Toast.LENGTH_LONG).show();
                                        SetClubInfo();
                                        checkClubAndUser();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "카카오 정보 설정 실패!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                return null;
            }
        });

    }

    private Task<String> getFirebaseJwt(final String kakaoAccessToken) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://us-central1-goput-aab5a.cloudfunctions.net/kakaoCustomAuth";
        HashMap<String, String> validationObject = new HashMap<>();
        validationObject.put("token", kakaoAccessToken);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(validationObject), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String firebaseToken = response.getString("firebase_token");
                    source.setResult(firebaseToken);
                } catch (Exception e) {
                    source.setException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                source.setException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", kakaoAccessToken);
                return params;
            }
        };
        queue.add(request);
        return source.getTask();
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signIn:
                userLogin();
                break;
            case R.id.findPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
        }
    }

    private void userLogin() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassWord.getText().toString().trim();

        if (email.isEmpty()) {
            loginEmail.setError("이메일을 입력하세요!");
            loginEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("이메일 형식을 확인하세요!");
            loginEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            loginPassWord.setError("패스워드를를 입력하세요!");
            loginPassWord.requestFocus();
            return;
        }

        showProgress(true);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //이메일 인증이 완료된 사용자만 로그인 가능

                    if (fUser.isEmailVerified()) {
                        SetClubInfo();
                        checkClubAndUser();
                    } else {
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, "이메일을 확인해주세요!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, "로그인을 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void checkClubAndUser() {
//        Log.d(TAG,"KAKAO : "+user.getUid());
        dbff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int flag = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ClubInfo clubInfo = dataSnapshot.getValue(ClubInfo.class);

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (clubInfo.id.equals(firebaseUser.getUid())) {
                        Toast.makeText(getApplicationContext(), "현재 계정에 클럽이 존재합니다.", Toast.LENGTH_SHORT).show();
                        finish();
                        finishFromLogin.finish();
                        startActivity(new Intent(getApplicationContext(), InfoAcitivity.class));
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        flag = 1;
                        showProgress(false);
                        break;
                    }
                }
                if (flag == 0) {
                    Intent intent = new Intent(getApplicationContext(), MakeOrJoinActivity.class);
                    intent.putExtra("valueFromLogin", 1001);
                    finish();
                    finishFromLogin.finish();
                    startActivity(intent);
                    showProgress(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //카카오톡 계정 연동을 위한 key값을 받아오는 메소드
    public static String getKeyHash(final Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (packageInfo == null)
                return null;

            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void SetClubInfo() {
        databaseReference = database.getReference("ClubInfo");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ClubInfo clubInfoReset = dataSnapshot.getValue(ClubInfo.class);
                        if (clubInfoReset.getId()!=null && clubInfoReset.getId().equals(user.getUid()) ) {
                            clubInfo.setClubInfo(dataSnapshot.getKey(), user.getUid(), clubInfoReset.getClubName(), clubInfoReset.getMajor(), clubInfoReset.getAgeGroup(), clubInfoReset.getAbility());
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putString("myClubKey",dataSnapshot.getKey());
                            edit.putString("myClubUid",user.getUid());
                            edit.putString("myClubName",clubInfoReset.getClubName());
                            edit.putString("myClubMajor",clubInfoReset.getMajor());
                            edit.putString("myClubAgeGroup",clubInfoReset.getAgeGroup());
                            edit.putString("myClubAbility",clubInfoReset.getAbility());
                            edit.apply();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}