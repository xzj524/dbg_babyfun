package com.xzj.babyfun.baseheader;

import android.os.Parcel;
import android.os.Parcelable;

import com.xzj.babyfun.logging.SLog;

public class BaseL2Message implements Parcelable{
    private static final String TAG = BaseL2Message.class.getSimpleName();
    public short commanID;
    public short versionCode;
    public byte[] payload;
    
    public byte[] toByte() {
        byte[] arrayByteL2 = null;
        try {
            arrayByteL2 = new byte[2 + payload.length];
            arrayByteL2[0] = (byte) (commanID & 0xff);
            arrayByteL2[1] = (byte) ((versionCode & 0x0f) << 4); 
            System.arraycopy(payload, 0, arrayByteL2, 2, payload.length);
        } catch (Exception e) {
            // TODO: handle exception
            SLog.e(TAG, e);
        }   
        return arrayByteL2;
    }
    
    

 
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        
    }

}
