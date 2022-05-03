package com.example.goputapplication;


import androidx.annotation.Nullable;

public class GoPutUser {
    public String registName,registAge,registPhoneNum,registId;

    public GoPutUser(){

    }
    public GoPutUser(String registName, @Nullable String registAge, @Nullable String registPhoneNum, String registId ){
        this.registName = registName;
        this.registAge = registAge;
        this.registPhoneNum = registPhoneNum;
        this.registId = registId;
    }
 }
