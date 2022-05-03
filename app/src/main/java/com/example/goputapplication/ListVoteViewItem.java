package com.example.goputapplication;

import android.widget.Button;

public class ListVoteViewItem {
    private String datevote;
    private String countvote;
    private Button member;
private String popupdata;
    public String getPopupdata() {
        return popupdata;
    }
    public Button getMember() {
        return member;
    }

    public void setMember(Button member,String popupdata) {
        this.member = member;
        this.popupdata = popupdata;
    }


    public String getDatevote() {
        return datevote;
    }

    public void setDatevote(String datevote) {
        this.datevote = datevote;
    }

    public String getCountvote() {
        return countvote;
    }

    public void setCountvote(String countvote) {
        this.countvote = countvote;
    }
}
