package com.example.goputapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goputapplication.ListAllMemberViewItem;
import com.example.goputapplication.R;
import com.example.goputapplication.model.MainNoticeItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainNoticeAdapter extends RecyclerView.Adapter<MainNoticeAdapter.CustomViewHolder> {
    //FireBase 연동
    private FirebaseDatabase database;
    private DatabaseReference dbMainNotice;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    private ArrayList<MainNoticeItem> arrayList;

    public MainNoticeAdapter(ArrayList<MainNoticeItem> arrayList) {
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public MainNoticeAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mainnoticlistview,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainNoticeAdapter.CustomViewHolder holder, int position) {
        holder.category.setText(arrayList.get(position).getCategory());
        holder.title.setText(arrayList.get(position).getTitle());
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView category,title;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.category = (TextView) itemView.findViewById(R.id.category);
            this.title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public void addItem(String category, String title){
        MainNoticeItem item = new MainNoticeItem();
        item.setCategory("["+category+"]");
        item.setTitle(title);
        arrayList.add(item);
    }


}
