package com.xzj.babyfun.sleepdatabase;



public class SleepInfoEnumClass {

    //SleepInfoId, SleepTimestamp, SleepDeviceTime, SleepValue;
    private long mSleepTimestamp = 0;
    private int mSleepDeviceTime = 0;
    private int mSleepValue = 0;
//    private String pushcurpkgname = "";
//    private String pushwebappbindinfo = "";
//    private String pushlightappbindinfo = "";
//    private String pushsdkclientbindinfo = "";
//    private String pushclientsbindinfo = "";
//    private String pushselfbindinfo = "";
    
    public long getSleepTimestamp() {
        return mSleepTimestamp;
    }

    public void setSleepTimestamp(long sleeptimestamp) {
        this.mSleepTimestamp = sleeptimestamp;
    }
    
    public int getSleepDeviceTime() {
        return mSleepDeviceTime;
    }

    public void setSleepDeviceTime(int sleepdevicetime) {
        this.mSleepDeviceTime = sleepdevicetime;
    }

    public int getSleepValue() {
        return mSleepValue;
    }
    
    public void setSleepValue(int  sleepvalue) {
        this.mSleepValue = sleepvalue;
    }

    
}
