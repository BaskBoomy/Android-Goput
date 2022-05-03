package com.example.goputapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.goputapplication.Notification.MySingleton;
import com.example.goputapplication.Notification.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchClubActivity extends AppCompatActivity {

    private final String TAG ="SEARCHCLUBACTIVITYTAG";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAL5OpZuE:APA91bFWWSL_dN2qCkdJxADxXlyvJkOgkV_IR1nSQOhpJdrm7fVG3-hBGvNpy0_ro3UbpWGxfdd-W7mIgB_2QALAwCavmVmxraskujQzbsHQojtKVrSJG3-fypz1-Zr9Q032YcbhHDx5";
    final private String contentType = "application/json";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOKEN;


    final int ACCESSCODE = 2020;
    EditText searchTxt;
    RecyclerView result_list;
    ImageButton searchBtn;
    FirebaseRecyclerOptions<SearchClubItem> options;
    private DatabaseReference mSearchClubDatabase,dbUsers;
    FirebaseRecyclerAdapter<SearchClubItem, SearchViewHolder> firebaseRecyclerAdapter;

    //요청 보내기 기능
    FirebaseUser fuser;
//    APIService apiService;
    boolean notify = false;
    @Override
    protected void onStart() {
        super.onStart();
        firebaseClubSearch("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseClubSearch("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_club);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        updateToken(FirebaseInstanceId.getInstance().getToken());
        mSearchClubDatabase = FirebaseDatabase.getInstance().getReference("ClubInfo");

        searchTxt = (EditText) findViewById(R.id.searchTxt);
        searchBtn = (ImageButton) findViewById(R.id.searchBtn);
        result_list = (RecyclerView) findViewById(R.id.result_list);

        result_list.setHasFixedSize(true);
        result_list.setLayoutManager(new LinearLayoutManager(this));
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchTxt.getText().toString();

                if (searchText.equals("") == false) {
                    firebaseClubSearch(searchText);
                } else {
                    firebaseClubSearch("");
                }
            }
        });

    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }
    private void firebaseClubSearch(String searchText) {
        Query query = mSearchClubDatabase.orderByChild("clubName").startAt(searchText).endAt(searchText + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<SearchClubItem>().setQuery(query, SearchClubItem.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SearchClubItem, SearchViewHolder>(options) {
            @NonNull
            @Override
            public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.clublistlayout, parent, false);

                return new SearchViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull SearchViewHolder holder, int position, @NonNull SearchClubItem model) {

//                Log.d("name : ", );
                holder.setDetailList(getApplicationContext(), model.getClubName(), model.getId());
            }
        };
        firebaseRecyclerAdapter.startListening();
        result_list.setAdapter(firebaseRecyclerAdapter);
    }


    public class SearchViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView club_Name, id;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notify = true;

                    openDialog(club_Name.getText().toString(),id.getText().toString());
                }
            });
        }

        public void setDetailList(Context context, String clubName, String user_Id) {
            club_Name = (TextView) mView.findViewById(R.id.srchClubName);
            id = (TextView) mView.findViewById(R.id.user_Id);
            club_Name.setText(clubName);
            id.setText(user_Id);
        }

        public void openDialog(String clubName, String clubLeaderId){
            JoinClubDialog joinClubDialog = new JoinClubDialog().newInstance(clubName,clubLeaderId);
            joinClubDialog.show(getSupportFragmentManager(),"클럽참가");
        }

    }
}