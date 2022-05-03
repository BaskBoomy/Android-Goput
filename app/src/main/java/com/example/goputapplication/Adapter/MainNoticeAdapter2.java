package com.example.goputapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goputapplication.R;
import com.example.goputapplication.model.MainNoticeItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainNoticeAdapter2 extends RecyclerView.Adapter<MainNoticeAdapter2.CustomViewHolder> {
    //FireBase 연동
    private FirebaseDatabase database;
    private DatabaseReference dbMainNotice;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    private ArrayList<MainNoticeItem> arrayList;

    public MainNoticeAdapter2(ArrayList<MainNoticeItem> arrayList) {
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public MainNoticeAdapter2.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mainnoticlistview2,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainNoticeAdapter2.CustomViewHolder holder, int position) {
        holder.category.setText(arrayList.get(position).getCategory());
        holder.title.setText(arrayList.get(position).getTitle());
        holder.content.setText(arrayList.get(position).getContent());
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView category,title,content;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.category = (TextView) itemView.findViewById(R.id.category);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.content = (TextView) itemView.findViewById(R.id.content);
        }
    }

    public void addItem(String category, String title,String content){
        MainNoticeItem item = new MainNoticeItem();
        item.setCategory("["+category+"]");
        item.setTitle(title);
        item.setContent(content);
        arrayList.add(item);
    }


}
