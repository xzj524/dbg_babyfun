package com.aizi.yingerbao.baseheader;

import com.aizi.yingerbao.logging.SLog;

public class BaseL1Message {
     
    private static final String TAG = BaseL1Message.class.getSimpleName();
    public boolean isAiziBaseL1Head;
    public boolean isNeedAck;
    public short errFlag;
    public short ackFlag;
    public short version;
    public short payloadLength;
    public short sequenceId;
    public short CRC16;
    
    public byte payload[];
    
    public byte[] tobyte() {
        byte[] arrayByteL1 = null;
        try {
            arrayByteL1 = new byte[6 + payload.length];
            if (!isAiziBaseL1Head) {
                return null;
            }
            arrayByteL1[0] = (byte) (((0xb & 0x0f) << 4) & 0xf0);
            
            if (isNeedAck) {
                arrayByteL1[1] = (byte) ((1 << 8) & 0x80);
            } else {
                arrayByteL1[1] = (byte) (0); 
            }
            
            arrayByteL1[1] = (byte) (((errFlag & 0x03) << 5) | arrayByteL1[1]);
            arrayByteL1[1] = (byte) (((ackFlag & 0x01) << 4) | arrayByteL1[1]); 
            arrayByteL1[1] = (byte) ((version & 0x0f) | arrayByteL1[1]);
            arrayByteL1[2] = (byte) (((payloadLength & 0x0f) << 4) & 0xf0);
            arrayByteL1[3] = (byte) (((sequenceId & 0x0f) << 4) & 0xf0);
            arrayByteL1[4] = (byte) ((CRC16 & 0xff00) >> 8);
            arrayByteL1[5] = (byte) (CRC16 & 0xff);
            System.arraycopy(payload, 0, arrayByteL1, 6, payload.length);
        } catch (Exception e) {
            // TODO: handle exception
            SLog.e(TAG, e);
        }
       
        return arrayByteL1;  
    }

}
