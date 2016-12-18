package com.aizi.yingerbao.userdatabase;

import cn.bmob.v3.BmobUser;


public class UserAccountInfo extends BmobUser{
    
    private String mUserName;
    private String mUserPassWord;
    public long mUserTimestamp = 0;
    private String mUserPosition;
    private String mBabySex;
    private int mBabyAgeMonth = 0;
    
    public String getUserName() {
        return this.mUserName;
    }

    public void setUserName(String name) {
        this.mUserName = name;
    }
    
    public String getUserPassWord() {
        return this.mUserPassWord;
    }

    public void setUserPassWord(String password) {
        this.mUserPassWord = password;
    }

    
    public String getUserPosition() {
        return this.mUserPosition;
    }

    public void setUserPosition(String position) {
        this.mUserPosition = position;
    }

    
    public String getBabySex() {
        return this.mBabySex;
    }

    public void setBabySex(String sex) {
        this.mBabySex = sex;
    }

    
    public int getBabyAgeMonth() {
        return this.mBabyAgeMonth;
    }

    public void setBabyAgeMonth(int month) {
        this.mBabyAgeMonth = month;
    }


}
