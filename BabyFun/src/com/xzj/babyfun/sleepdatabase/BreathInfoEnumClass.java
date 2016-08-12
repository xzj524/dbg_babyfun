package com.xzj.babyfun.sleepdatabase;

public class BreathInfoEnumClass {
    public long mBreathTimestamp = 0;
    public int mBreathIsAlarm = 0;
    public int mBreathDuration = 0;
    
    
    public long getBreathTimestamp() {
        return mBreathTimestamp;
    }

    public void setBreathTimestamp(long breathtimestamp) {
        this.mBreathTimestamp = breathtimestamp;
    }
    
    public int getBreathIsAlarm() {
        return mBreathIsAlarm;
    }

    public void setBreathIsAlarm(int breathisalarm) {
        this.mBreathIsAlarm = breathisalarm;
    }

    public int getBreathDuration () {
        return mBreathDuration;
    }
    
    public void setBreathDuration (int breathduration) {
        this.mBreathDuration = breathduration;
    }

}
