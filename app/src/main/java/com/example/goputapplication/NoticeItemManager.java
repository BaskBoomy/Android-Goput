package com.example.goputapplication;

import java.io.Serializable;

public class NoticeItemManager implements Serializable {
    NoticeItem noticeItem = new NoticeItem();

    public void setNotice(String price,String maxPeople,int ACCESSCODE){
        noticeItem.setPrice(price);
        noticeItem.setMaxPeople(maxPeople);
        noticeItem.setACCESSCODE(ACCESSCODE);
    }

    public NoticeItem getNotice(){
        return noticeItem;
    }
}
