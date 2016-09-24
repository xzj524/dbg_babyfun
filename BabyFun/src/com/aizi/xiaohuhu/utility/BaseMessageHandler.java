package com.aizi.xiaohuhu.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;

import com.aizi.xiaohuhu.baseheader.BaseL1Message;
import com.aizi.xiaohuhu.baseheader.BaseL2Message;
import com.aizi.xiaohuhu.baseheader.KeyPayload;
import com.aizi.xiaohuhu.constant.Constant;
import com.aizi.xiaohuhu.crc.CRC16;
import com.aizi.xiaohuhu.eventbus.AsycEvent;
import com.aizi.xiaohuhu.logging.SLog;

import de.greenrobot.event.EventBus;

public class BaseMessageHandler {
    
    private static final String TAG = BaseMessageHandler.class.getSimpleName();
    private final static short BASE_DATA_HEAD = 6;
    
    private static boolean isOver;
    public static ByteArrayOutputStream l2OutputStream = new ByteArrayOutputStream();
    public static ByteArrayInputStream l2InputStream;
    public static boolean isWriteSuccess;
    static byte[] baseTwobytes;
    public static int squenceID = 1;
    
    public static void acquireBaseData(BluetoothDevice bluetoothDevice, 
            BluetoothGattCharacteristic characteristic) {
        if (Constant.BLE_UUID_NUS_RX_CHARACTERISTIC.equals(characteristic.getUuid())) {
            byte[] baseData = characteristic.getValue();
            if (baseData == null) {
                return;
            }
            SLog.e(TAG, "print base l1 log  = " + Arrays.toString(baseData));
            if (baseData.length >= BASE_DATA_HEAD) {
                BaseL1Message bsL1Msg = getBaseL1Msg(baseData); // 生成L1数据
                if (bsL1Msg.ackFlag == 1) { //收到设备的ack信息
                    SLog.e(TAG, "receive ACK");
                    if (bsL1Msg.errFlag == 0) {
                        if (isWriteSuccess) {
                            SLog.e(TAG, "isWriteSuccess = true");
                           // boolean isSendL2Over = sendL2Msg(false);
                        }else {
                            //重写接口
                            
                        }
                    }
                }else {
                    if (bsL1Msg.isNeedAck && bsL1Msg.ackFlag == 0) { //收到设备发过来的信息，需要返回ACK
                        int crc16 = CRC16.calcCrc16(bsL1Msg.payload);
                        SLog.e(TAG, "ACK crc16 = " + crc16 + " bsL1Msg = " + bsL1Msg.CRC16);
                        if (bsL1Msg.CRC16 == (short)crc16) {
                            SLog.e(TAG, "send ACK");
                            sendACKBaseL1Msg(baseData);
                        } else {
                            SLog.e(TAG, "not send ACK");
                        }
                    }  
                    generateBaseL2MsgByteArray(bsL1Msg); // 生成L2所需要的byte数组
                    if (isOver) {
                        isOver = false;
                        if (l2OutputStream.size() > 0) {
                            BaseL2Message bsl2Msg = getBaseL2Msg(l2OutputStream.toByteArray()); 
                            l2OutputStream.reset();
                            
                            Intent l2intent = new Intent();
                            l2intent.putExtra(Constant.BASE_L2_MESSAGE, bsl2Msg);
                            EventBus.getDefault().post(bsl2Msg);
                            SLog.e(TAG, "receive L2 DATA");
                        }            
                    }
                } 
                
            }
        }  
    }

    private static void sendACKBaseL1Msg(byte[] baseData) {
        // TODO Auto-generated method stub
        byte[] ACKMsg = new byte[6];
        System.arraycopy(baseData, 0, ACKMsg, 0, 6);
        ACKMsg[1] = (byte) ((baseData[1] | 0x10) & 0x10);
        ACKMsg[2] = (byte) (baseData[2] & 0);
        ACKMsg[4] = (byte) (baseData[4] & 0);
        ACKMsg[5] = (byte) (baseData[5] & 0);
        
        EventBus.getDefault().post(new AsycEvent(ACKMsg)); 
    }
    
    public static boolean sendL2Message(BaseL2Message bsl2msg) {
        boolean isSendL2Over = false;
        try {
            
            
            String l2payload = MessageParse.printHexString(bsl2msg.toByte());
            SLog.e(TAG, "HEX Send string l2load1 = " + l2payload);
            Intent intent = new Intent(Constant.DATA_TRANSFER_SEND);
            intent.putExtra("transferdata", l2payload);
            EventBus.getDefault().post(intent); 
            
            l2InputStream = new ByteArrayInputStream(bsl2msg.toByte());
            if (l2InputStream != null) {
                isSendL2Over = sendL2Msg(true);
            }
        } catch (Exception e) {
            // TODO: handle exception
            SLog.e(TAG, e);
        }
        
        return isSendL2Over;
    }
    
    public static boolean sendL2Msg(boolean isstart) {
        byte[] buffer = new byte[14];
        byte[] sendbuff = null;
        int flag = 0;
        boolean isSendL2Over = false;
        
        try {
            if (l2InputStream.available() > 0) {
                int readcount = l2InputStream.read(buffer, 0, 14);
                if (readcount > 0 && readcount <= 14) {
                    sendbuff = new byte[readcount];
                    System.arraycopy(buffer, 0, sendbuff, 0, readcount);
                    if (l2InputStream.available() > 0) { //读取14个字符之后还有内容
                        if (isstart) {
                            flag = 0; //开始帧
                        } else {
                            flag = 1; //中间帧
                        }
                    } else {
                        flag = 2;//结束帧
                        l2InputStream.close();
                        isSendL2Over = true;
                    }
                    sendL1Msg(sendbuff, flag);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            SLog.e(TAG, e);
            try {
                l2InputStream.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        
        return isSendL2Over;     
    }
    
/*    public static void sendL2EndMsg() {
        // TODO Auto-generated method stub
        BaseL1Message bsOMsg = new BaseL1Message();
        byte[] buffer = new byte[14];
        int readcount = in.read(buffer, 0, 14);
        SLog.e(TAG, "readcount = " + readcount);
        bsOMsg.payload = new byte[readcount];
        System.arraycopy(buffer, 0, bsOMsg.payload, 0, readcount);
        if (readcount < 14) {
            bsOMsg.errFlag = 2;
        } else {
            bsOMsg.errFlag = 1;
        }
        bsOMsg.isAiziBaseL1Head = true;
        bsOMsg.isNeedAck = true;
        bsOMsg.ackFlag = 0;
        bsOMsg.version = 0;
        bsOMsg.payloadLength = (short) readcount;
        bsOMsg.sequenceId = (short) ++squenceID;
        bsOMsg.CRC16 = (short) CRC16.calcCrc16(bsOMsg.payload);
        
        SLog.e(TAG, "write BASE character readcount = " + readcount);
        if (readcount > 0) {
            EventBus.getDefault().post(new AsycEvent(bsOMsg.tobyte()));
        }
    }*/
    
    public static void sendL1Msg(byte[] buffer, int flag) {
        // TODO Auto-generated method stub
        BaseL1Message bsL1Msg = new BaseL1Message();
        if (buffer.length > 0) {
            SLog.e(TAG, "readcount = " + buffer.length);
            bsL1Msg.payload = new byte[buffer.length];
            System.arraycopy(buffer, 0, bsL1Msg.payload, 0, buffer.length);
            
            bsL1Msg.errFlag = (short) flag;
            bsL1Msg.isAiziBaseL1Head = true;
            bsL1Msg.isNeedAck = true;
            bsL1Msg.ackFlag = 0;
            bsL1Msg.version = 0;
            bsL1Msg.payloadLength = (short) buffer.length;
            bsL1Msg.sequenceId = (short) ++squenceID;
            bsL1Msg.CRC16 = (short) CRC16.calcCrc16(bsL1Msg.payload);
            
            byte[] bsl1buffer = bsL1Msg.tobyte();
            SLog.e(TAG, "write BASE character readcount = " + buffer.length);
            EventBus.getDefault().post(new AsycEvent(bsL1Msg.tobyte()));
        }
    }

    private static void generateBaseL2MsgByteArray(BaseL1Message bsL1Msg) {
        try {
            if (bsL1Msg.isAiziBaseL1Head) {
                if (bsL1Msg.errFlag == 0) { // 标识起始位
                    l2OutputStream.reset();
                    l2OutputStream.write(bsL1Msg.payload);
                    isOver =false;
                } else if (bsL1Msg.errFlag == 1) { // 标识中间位
                    l2OutputStream.write(bsL1Msg.payload);
                    isOver =false;
                } else if (bsL1Msg.errFlag == 2) { // 标识结束位
                    isOver = true;
                  //  l2OutputStream.reset();
                    l2OutputStream.write(bsL1Msg.payload);
                } else {
                    isOver = false;
                }
                l2OutputStream.close();
            } else {
                isOver = false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            SLog.e(TAG, e);
        }  
    }

    public static BaseL2Message generateBaseL2Msg(short commanid, short version, 
            KeyPayload keyPayload){
        BaseL2Message bsl2Msg = new BaseL2Message();
        bsl2Msg.commanID = commanid;
        bsl2Msg.versionCode = version;
        bsl2Msg.payload = new byte[keyPayload.keyLen + 3];
        System.arraycopy(keyPayload.toByte(), 0, bsl2Msg.payload, 0, keyPayload.keyLen + 3);
        return bsl2Msg;
        
    }

    private static BaseL2Message getBaseL2Msg(byte[] l2data) {
        // TODO Auto-generated method stub
        BaseL2Message bsl2Msg = new BaseL2Message();
        if (l2data.length > 1) {
            bsl2Msg.commanID = (short)(l2data[0] & 0xff); 
            bsl2Msg.versionCode = (short) ((l2data[1] & 0xf0) >> 4);
            int len = l2data.length;
            bsl2Msg.payload = new byte[len-2];
            System.arraycopy(l2data, 2, bsl2Msg.payload, 0, len - 2); // 拷贝l2数据
        }   
        return bsl2Msg;
    }

    private static BaseL1Message getBaseL1Msg(byte[] basedata) {
        BaseL1Message bsL1Msg = new BaseL1Message();
        // TODO Auto-generated method stub
        if ((basedata[0] & 0xf0) == 0xb0) {
            bsL1Msg.isAiziBaseL1Head = true;
        } else {
            bsL1Msg.isAiziBaseL1Head = false;
        }
        
        if ((basedata[1] & 0x80) == 0x80) {
            bsL1Msg.isNeedAck = false;
        }else {
            bsL1Msg.isNeedAck = true;
        }
        
        bsL1Msg.errFlag = (short) ((basedata[1] & 0x60) >> 5);
        bsL1Msg.ackFlag = (short) ((basedata[1] & 0x10) >> 4);
        bsL1Msg.version = (short) ((basedata[1] & 0x0f) >> 4);
        
        bsL1Msg.payloadLength = (short) ((basedata[2] & 0xf0) >> 4);
        bsL1Msg.sequenceId = (short) ((basedata[3] & 0xf0) >> 4);
        bsL1Msg.CRC16 =  (short) (((basedata[4] & 0xff) << 8) | (basedata[5] & 0xff));
        if (bsL1Msg.payloadLength > 0) {
            bsL1Msg.payload = new byte[bsL1Msg.payloadLength];
            System.arraycopy(basedata, 6,
                    bsL1Msg.payload, 0, bsL1Msg.payloadLength);
        }   
        
        return bsL1Msg;
    }
}
