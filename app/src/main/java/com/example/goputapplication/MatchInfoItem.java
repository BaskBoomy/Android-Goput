package com.example.goputapplication;

import java.io.Serializable;

public class MatchInfoItem implements Serializable {
    String clubId,section, matchDate, voteDueDate, place, address,price, vsClubName, maxPeople, dinner;
    int voteTrue,voteFalse;

    public int getVoteTrue() {
        return voteTrue;
    }

    public void setVoteTrue(int voteTrue) {
        this.voteTrue = voteTrue;
    }

    public int getVoteFalse() {
        return voteFalse;
    }

    public void setVoteFalse(int voteFalse) {
        this.voteFalse = voteFalse;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getVoteDueDate() {
        return voteDueDate;
    }

    public void setVoteDueDate(String voteDueDate) {
        this.voteDueDate = voteDueDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVsClubName() {
        return vsClubName;
    }

    public void setVsClubName(String vsClubName) {
        this.vsClubName = vsClubName;
    }

    public String getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(String maxPeople) {
        this.maxPeople = maxPeople;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }
}
