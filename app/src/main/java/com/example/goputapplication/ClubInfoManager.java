package com.example.goputapplication;

public class ClubInfoManager {
    ClubInfo clubInfo = new ClubInfo();

    public void setClubInfo(String clubId,String userId, String clubName,String major, String ageGroup, String ability){
        clubInfo.setClubId(clubId);
        clubInfo.setId(userId);
        clubInfo.setClubName(clubName);
        clubInfo.setMajor(major);
        clubInfo.setAgeGroup(ageGroup);
        clubInfo.setAbility(ability);
    }

    public ClubInfo getClubInfo(){
        return clubInfo;
    }
}
