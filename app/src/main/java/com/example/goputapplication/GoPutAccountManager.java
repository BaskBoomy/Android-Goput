package com.example.goputapplication;

public class GoPutAccountManager {
    AccountItem accountItem = new AccountItem();

    public void setAccount(String id, String nickName, String profileImg){
        accountItem.setId(id);
        accountItem.setNickName(nickName);
        accountItem.setProfileImg(profileImg);
    }

    public AccountItem getAccount(){
        return accountItem;
    }


}
