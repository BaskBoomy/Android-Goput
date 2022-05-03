package com.example.goputapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.goputapplication.Notification.MySingleton;
import com.example.goputapplication.Notification.Token;
import com.example.goputapplication.model.InviteNotificationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class JoinClubDialog extends AppCompatDialogFragment {
    private final String TAG = "JOINCLUBDIALOG";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAL5OpZuE:APA91bFWWSL_dN2qCkdJxADxXlyvJkOgkV_IR1nSQOhpJdrm7fVG3-hBGvNpy0_ro3UbpWGxfdd-W7mIgB_2QALAwCavmVmxraskujQzbsHQojtKVrSJG3-fypz1-Zr9Q032YcbhHDx5";
    final private String contentType = "application/json";

    private DatabaseReference notificationDatabase, userDatabase, databaseReference,dbUsers,dbClubInfo;
    private FirebaseUser fuser;

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOKEN;

    TextView clubName;

    Context context;
    String clubLeaderId;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;
    Boolean joinedClub;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.joinclubdialog, null);

        context = view.getContext();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();

        clubName = (TextView) view.findViewById(R.id.clubName);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        String dataClubName = getArguments().getString("clubName");
        clubLeaderId = getArguments().getString("clubLeaderId");
        clubName.setText(dataClubName);

        builder.setView(view)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edit.putBoolean("joinedClub",false);
                        edit.apply();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("요청", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkMyClub();
                        joinedClub = sharedPreferences.getBoolean("joinedClub", false);
                        //클럽 리더 TOKEN 받아오기
                        notificationDatabase = FirebaseDatabase.getInstance().getReference("Tokens");
                        notificationDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Boolean myClubChk = false;
                                if(joinedClub == true){
                                    Toast.makeText(context, "이미 참여한 클럽입니다!!", Toast.LENGTH_SHORT).show();
                                    edit.putBoolean("joinedClub",false);
                                    edit.apply();
                                    myClubChk = true;
                                }
                                if (clubLeaderId.equals(fuser.getUid())) {
                                    Toast.makeText(context, "본인 클럽입니다!", Toast.LENGTH_SHORT).show();
                                    myClubChk = true;
                                }
                                if (myClubChk == false) {
                                    loopOut:
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if (dataSnapshot.getKey().equals(clubLeaderId)) {
                                            Token token = dataSnapshot.getValue(Token.class);
                                            userDatabase = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                                            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    GoPutUser goPutUser = snapshot.getValue(GoPutUser.class);
                                                    InviteNotificationItem inviteNotification = new InviteNotificationItem();
                                                    inviteNotification.setProfileImg("gs://goput-aab5a.appspot.com/2019-08-04-13-26-51[1].jpg");
                                                    inviteNotification.setName(goPutUser.registName);
                                                    inviteNotification.setSenderId(fuser.getUid());
                                                    inviteNotification.setReceiverId(clubLeaderId);
                                                    inviteNotification.setMainText("클럽 참가요청 메세지");
                                                    databaseReference = FirebaseDatabase.getInstance().getReference("Notification");
                                                    ValueEventListener valueEventListener = new ValueEventListener() {
                                                        Boolean chk = false;
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                                                ListNotiInviteViewItem item = dataSnapshot1.getValue(ListNotiInviteViewItem.class);
                                                                if (item.getReceiverId().equals(clubLeaderId)&&item.getSenderId().equals(fuser.getUid())) {
                                                                    failToast();
                                                                    chk = true;
                                                                    break;
                                                                }
                                                            }

                                                            if (chk == false) {
                                                                successToast();
                                                                databaseReference.push().setValue(inviteNotification);
                                                                //TOKEN : 알림을 받을 사용자의 TOKEN = 클럽 리더의 TOKEN
                                                                TOKEN = token.getToken();
                                                                NOTIFICATION_TITLE = "클럽 참가 요청";
                                                                NOTIFICATION_MESSAGE = goPutUser.registName + "님 초대수락하기";

                                                                JSONObject notification = new JSONObject();
                                                                JSONObject notifcationBody = new JSONObject();
                                                                try {
                                                                    notifcationBody.put("title", NOTIFICATION_TITLE);
                                                                    notifcationBody.put("message", NOTIFICATION_MESSAGE);
                                                                    notification.put("to", TOKEN);
                                                                    notification.put("data", notifcationBody);
                                                                } catch (JSONException e) {
                                                                    Log.e(TAG, "onCreate: " + e.getMessage());
                                                                }
                                                                sendNotification(notification);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                        }
                                                    };
                                                    databaseReference.addListenerForSingleValueEvent(valueEventListener);
                                                    databaseReference.removeEventListener(valueEventListener);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                });
        return builder.create();
    }

    private void failToast() {
        Toast.makeText(context, "이미 요청을 했습니다!", Toast.LENGTH_SHORT).show();
    }

    private void successToast() {
        Toast.makeText(context, "요청을 전송하였습니다!", Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static JoinClubDialog newInstance(String clubName, String clubLeaderId) {
        JoinClubDialog fragment = new JoinClubDialog();

        Bundle bundle = new Bundle();
        bundle.putString("clubName", clubName);
        bundle.putString("clubLeaderId", clubLeaderId);
        fragment.setArguments(bundle);

        return fragment;
    }

    public void checkMyClub(){
        //선택한 클럽이 myClub에 있는지 확인하면됨
        dbUsers = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("myclub");
        dbClubInfo = FirebaseDatabase.getInstance().getReference("ClubInfo");
        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String clubKey = String.valueOf(dataSnapshot.getValue());
                    dbClubInfo.orderByChild("id").equalTo(clubLeaderId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if(dataSnapshot.getKey().equals(clubKey)){
                                    edit.putBoolean("joinedClub",true);
                                    edit.apply();

                                    break;
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
    }
}
