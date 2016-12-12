package com.aizi.yingerbao.synctime;

import java.util.BitSet;

import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.BitSetConvert;


public class DeviceTime {
    
    private static final String TAG = DeviceTime.class.getSimpleName();
    
    public int year;
    public int month;
    public int day;
    public int hour;
    public int min;
    public int second;
    
    public long millisecondValue;
    
    private short mIndex = 0;
    private BitSet bitSet;
    
    public byte[] toByte() {
        byte[] devTimeValue = null;
        bitSet = new BitSet(32);
        try {
            calculateBitset(bitSet, year, 6);
            calculateBitset(bitSet, month, 4);
            calculateBitset(bitSet, day, 5);
            calculateBitset(bitSet, hour, 5);
            calculateBitset(bitSet, min, 6);
            calculateBitset(bitSet, second, 6);      
            devTimeValue = BitSetConvert.bitSet2ByteArray(bitSet);
        } catch (Exception e) {
            SLog.e(TAG, e);
        }   
        return devTimeValue;
    }

    private void calculateBitset(BitSet bitSet, int keyvalue, int keylength) {
        try {
            if (bitSet != null) {
                for (int i = keylength -1; i >= 0; i--) {
                    bitSet.set(mIndex++, (keyvalue & (1 << i)) >> i == 1 ? true
                            : false);
                } 
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }
    
    private boolean checkDeviceTimeCorrect() {
        long offsettime = Math.abs(System.currentTimeMillis() - millisecondValue);
        return offsettime < 60 * 60 * 1000 ? true : false;
        
    }
}
