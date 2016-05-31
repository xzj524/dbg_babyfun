package com.xzj.babyfun.baseheader;

import com.xzj.babyfun.logging.SLog;

public class KeyPayload {
    
    private static final String TAG = KeyPayload.class.getSimpleName();
    public short key;
    public short keyLen;
    public byte[] keyValue;
    
    
    public byte[] toByte() {
        byte[] keyPayload = null;
        try {
            keyPayload = new byte[3 + keyLen];
            keyPayload[0] = (byte) (key & 0xff);
            keyPayload[1] = (byte) ((keyLen & 0x100) >> 8); 
            keyPayload[2] = (byte) (keyLen & 0xff); 
            System.arraycopy(keyValue, 0, keyPayload, 3, keyLen);
        } catch (Exception e) {
            // TODO: handle exception
            SLog.e(TAG, e);
        }   
        return keyPayload;
    }
}
