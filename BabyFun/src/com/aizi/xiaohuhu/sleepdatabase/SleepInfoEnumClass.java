package com.aizi.xiaohuhu.sleepdatabase;



public class SleepInfoEnumClass {

    private long mSleepTimestamp = 0;
    private int mSleepYear= 0;
    private int mSleepMonth= 0;
    private int mSleepDay= 0;
    private int mSleepMinute= 0;
    private int mSleepValue = 0;

    
    public long getSleepTimestamp() {
        return mSleepTimestamp;
    }

    public void setSleepTimestamp(long sleeptimestamp) {
        this.mSleepTimestamp = sleeptimestamp;
    }
    
    public int getSleepYear() {
        return mSleepYear;
    }

    public void setSleepYear(int sleepyear) {
        this.mSleepYear = sleepyear;
    }
    
    public int getSleepMonth() {
        return mSleepMonth;
    }

    public void setSleepMonth(int sleepmonth) {
        this.mSleepMonth = sleepmonth;
    }
    
    public int getSleepDay() {
        return mSleepDay;
    }

    public void setSleepDay(int sleepday) {
        this.mSleepDay = sleepday;
    }
    
    public int getSleepMinute() {
        return mSleepMinute;
    }

    public void setSleepMinute(int sleepminute) {
        this.mSleepMinute = sleepminute;
    }

    public int getSleepValue() {
        return mSleepValue;
    }
    
    public void setSleepValue(int  sleepvalue) {
        this.mSleepValue = sleepvalue;
    }
}
