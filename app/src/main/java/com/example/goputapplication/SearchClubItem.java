package com.example.goputapplication;

public class SearchClubItem {
    String clubName;

    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SearchClubItem(){

    }
    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public SearchClubItem(String clubName){
        this.clubName = clubName;
    }
}
