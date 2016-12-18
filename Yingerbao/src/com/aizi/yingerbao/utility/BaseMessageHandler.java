package com.aizi.yingerbao.utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;

import com.aizi.yingerbao.baseheader.BaseL1Message;
import com.aizi.yingerbao.baseheader.BaseL2Message;
import com.aizi.yingerbao.bluttooth.BluetoothApi;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.crc.CRC16;
import com.aizi.yingerbao.eventbus.AsycEvent;
import com.aizi.yingerbao.logging.SLog;

import de.greenrobot.event.EventBus;

public class BaseMessageHandler {
    
    private static final String TAG = BaseMessageHandler.class.getSimpleName();
    private static BaseMessageHandler mInstance;
    private final static short BASE_DATA_HEAD = 6;
    private final static short BASE_L2_DATA_LEN = 5;
    private final static short BASE_L2_RECE_LEN = 128;
    private static Object mYingerbaoLock = new Object();
    
    public static byte[] mL2RecvByte = new byte[BASE_L2_RECE_LEN];
    public static int mL2RecvCurrLen = 0;
    public static int repeattime = 0;
    public static boolean isWriteSuccess = true;
    public static int mL2SquenceID = 1;
    public static int mL1squenceid = -1;
    private static final long WAIT_PERIOD = 5 * 1000; //5 seconds
    static PendingIntent mL1PendingIntent;
    Context mContext;
    
    public BaseMessageHandler(Context context) {
        mContext = context;
    }
    
    public static BaseMessageHandler getInstance(Context context) {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new BaseMessageHandler(context);
            return mInstance;
        }
    }
    
    public void acquireBaseData(Context context, BluetoothDevice bluetoothDevice, 
            BluetoothGattCharacteristic characteristic) {
        try {
            if (Constant.BLE_UUID_NUS_RX_CHARACTERISTIC.equals(characteristic.getUuid())) {
                byte[] baseData = characteristic.getValue();
                if (baseData == null) {
                    clearL2RecvByte();
                    return;
                }
                
                if (baseData.length >= BASE_DATA_HEAD) {
                    BaseL1Message bsL1Msg = getBaseL1Msg(baseData); // 生成L1数据
                    String l1payload = Utiliy.printHexString(bsL1Msg.tobyte());
                    
                    if (bsL1Msg.ackFlag == 1) { //收到设备的ack信息
                        Utiliy.saveLog(" L1 RECV ACK: ", l1payload);
                        Intent intent = new Intent(Constant.DATA_TRANSFER_RECEIVE);
                        intent.putExtra("transferdata", " L1 " + " ACK: " + l1payload);
                        EventBus.getDefault().post(intent); // 显示到测试界面上
                    } else {
                        //收到设备发过来的信息，需要返回ACK
                        if (bsL1Msg.isNeedAck && bsL1Msg.ackFlag == 0) { 
                            // 判断接收的长度是否正确
                            boolean isLenRight = checkLen(bsL1Msg, baseData);
                            if (isLenRight) {
                                int crc16 = CRC16.calcCrc16(bsL1Msg.payload);
                                if (bsL1Msg.CRC16 == (short)crc16) {
                                    if (bsL1Msg.errFlag == 2) { //标识结束位
                                        mL1squenceid = -1;
                                    } else if (bsL1Msg.errFlag == 1) { // 标识中间位
                                        if ((mL1squenceid == 15 && bsL1Msg.sequenceId == 0) 
                                                || bsL1Msg.sequenceId - mL1squenceid == 1) {
                                            mL1squenceid = bsL1Msg.sequenceId;
                                        } else if (mL1squenceid == bsL1Msg.sequenceId) { // L1消息重复
                                            // 消息重复的情况也按照正确消息返回ACK
                                            Utiliy.saveLog(" L1 RECV REPEAT DATA: ", l1payload);

                                            Intent intent = new Intent(Constant.DATA_TRANSFER_RECEIVE);
                                            intent.putExtra("transferdata", " L1 REPEAT DATA: " + l1payload);
                                            EventBus.getDefault().post(intent); // 显示到测试界面上
                                            
                                            sendACKBaseL1Msg(context, baseData, 0); 
                                            return;
                                        } else if ((bsL1Msg.sequenceId - mL1squenceid != 1) 
                                                && mL1squenceid != bsL1Msg.sequenceId) {
                                            // 帧序号校验错误,L2缓存清空
                                            clearL2RecvByte();
                                            Utiliy.saveLog("L1 RECV SQUENCEID ERROR DATA: ", l1payload);
                                            Intent intent = new Intent(Constant.DATA_TRANSFER_RECEIVE);
                                            intent.putExtra("transferdata", " L1 SQUENCEID ERROR DATA: " + l1payload);
                                            EventBus.getDefault().post(intent); // 显示到测试界面上
                                            
                                            //sendACKBaseL1Msg(context, baseData, 1); // 
                                            return;//中间位与上一个相差不为1 则抛弃
                                        } 
                                    } else if (bsL1Msg.errFlag == 0) { // 标识起始位
                                        mL1squenceid = bsL1Msg.sequenceId;
                                    }
                                    
                                    // 设置超时机制，超时后所有接收的l1层数据清空
                                    if (mL1PendingIntent != null) {
                                        Utiliy.cancelAlarmPdIntent(context, mL1PendingIntent);
                                    }
                                    mL1PendingIntent = Utiliy.getDelayPendingIntent(context, Constant.ALARM_WAIT_L1);
                                    Utiliy.setDelayAlarm(context, WAIT_PERIOD, mL1PendingIntent);

                                    Utiliy.saveLog(" L1 RECV DATA: ", l1payload);
                                    Intent intent = new Intent(Constant.DATA_TRANSFER_RECEIVE);
                                    intent.putExtra("transferdata", " L1 DATA: " + l1payload);
                                    EventBus.getDefault().post(intent); // 显示到测试界面上
                                    
                                    sendACKBaseL1Msg(context, baseData, 0); // CRC校验、帧序号校验正确
                                    handleL1Msg(context, bsL1Msg);
                                  } else {
                                      clearL2RecvByte();
                                      //sendACKBaseL1Msg(context, baseData, 2); // CRC校验错误
                                      Utiliy.saveLog(" L1 RECV CRC ERROR DATA: ", l1payload);
                                      Intent intent = new Intent(Constant.DATA_TRANSFER_RECEIVE);
                                      intent.putExtra("transferdata", " L1 CRC ERROR DATA: " + l1payload);
                                      EventBus.getDefault().post(intent); // 显示到测试界面上
                                  }
                                } else {
                                    clearL2RecvByte();
                                    Utiliy.saveLog(" L1 RECV LENGTH ERROR DATA: ", l1payload);
                                    Intent intent = new Intent(Constant.DATA_TRANSFER_RECEIVE);
                                    intent.putExtra("transferdata", " L1 LENGTH ERROR DATA: " + l1payload);
                                    EventBus.getDefault().post(intent); // 显示到测试界面上
                                }             
                            } else {
                                SLog.e(TAG, "Not Send ACK");
                            }
                        }  
                    } else {
                        clearL2RecvByte();
                    } 
                }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }

    public static void clearL2RecvByte() {
        Arrays.fill(mL2RecvByte, (byte) 0);
        mL2RecvCurrLen = 0;
    }

    private boolean checkLen(BaseL1Message bsL1Msg, byte[] baseData) {
        int basedatalen = 0;
        try {
            if (baseData != null) {
                basedatalen = baseData.length; // 接收数据的长度
                if (basedatalen == BASE_DATA_HEAD + bsL1Msg.payloadLength) {
                    return true;
                }
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        return false;
    }

    private void handleL1Msg(Context context, BaseL1Message bsL1Msg) {
        try {
            int l2len = 0;
            boolean isReceOver = generateBaseL2MsgByteArray(bsL1Msg); // 生成L2所需要的byte数组
            if (isReceOver) {
                if (mL2RecvByte != null) {
                    String l2failedstr = Utiliy.printHexString(mL2RecvByte);
                    if (mL2RecvCurrLen > 5) {
                        l2len = ((mL2RecvByte[3] & 0x01) << 8) | (mL2RecvByte[4] & 0xff);
                        if (l2len + BASE_L2_DATA_LEN == mL2RecvCurrLen) {
                            BaseL2Message bsl2Msg = getBaseL2Msg(mL2RecvByte, mL2RecvCurrLen); 
                            MessageParse.getInstance(context).RecvBaseL2Msg(bsl2Msg);
                        } else {           
                            Utiliy.logToFile(" L2 RECV FAILED DATA(length is wrong) : " + l2failedstr);
                            SLog.e(TAG, " L2 RECV FAILED DATA(length is wrong) : "  + l2failedstr);
                        }
                     } else {
                         Utiliy.logToFile(" L2 RECV FAILED DATA(length is too short) : " + l2failedstr);
                         SLog.e(TAG, " L2 RECV FAILED DATA(length is too short) : "  + l2failedstr);
                    }
                }   
                SLog.e(TAG, "receiveL2DATA before l2len = " + l2len + " totallen = " + mL2RecvCurrLen);
                clearL2RecvByte();
            } else {
                SLog.e(TAG, "reflectTranDataType 1");
                Utiliy.reflectTranDataType(context, 1);
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }
        

    private void sendACKBaseL1Msg(Context context, byte[] baseData, int errcode) {
        byte[] ACKMsg = new byte[6];
        System.arraycopy(baseData, 0, ACKMsg, 0, 6);
        ACKMsg[1] = (byte) (((errcode & 0x03) << 5) & 0x60);
        ACKMsg[1] = (byte) (ACKMsg[1] | 0x10);
        ACKMsg[2] = (byte) (baseData[2] & 0);
        ACKMsg[4] = (byte) (baseData[4] & 0);
        ACKMsg[5] = (byte) (baseData[5] & 0);
        
        String l1ack = Utiliy.printHexString(ACKMsg);
        Utiliy.logToFile(" L1  SEND ACK: " + l1ack); // 写入日志文件
        SLog.e(TAG, " L1 SEND ACK: " + l1ack);
        Intent intent = new Intent(Constant.DATA_TRANSFER_SEND);
        intent.putExtra("transferdata", " L1 " + " ACK: " + l1ack);
        EventBus.getDefault().post(intent); // 显示到测试界面上
        AsycEvent asycEvent = new AsycEvent(ACKMsg);
        asycEvent.isAck = true;
        BluetoothApi.getInstance(context).RecvEvent(asycEvent);
    }
    
    public boolean sendL2Message(Context context, BaseL2Message bsl2msg, boolean isrepeat) {
        boolean isSendL2Over = false;
        String l2str = null;
        synchronized (mYingerbaoLock) {
            try {
                
                /****在测试界面显示出来,写入日志文件*****/
                String l2payload = Utiliy.printHexString(bsl2msg.toByte());

                Intent intent = new Intent(Constant.DATA_TRANSFER_SEND);
                intent.putExtra("transferdata", "L2 " + l2payload);
                EventBus.getDefault().post(intent);

                l2str = "L2 SEND: " + l2payload + " repeat = " + isrepeat;
                Utiliy.logToFile(l2str); 
                SLog.e(TAG, l2str);
                /****在测试界面显示出来,写入日志文件*****/
                
                sendL2Msg(context, bsl2msg, isrepeat);

            } catch (Exception e) {
                SLog.e(TAG, e);
            }
        }
        return isSendL2Over;
    }
    
    private boolean sendL2Msg(Context context, BaseL2Message bsl2msg, boolean isrepeat) {
        byte[] buffer = new byte[14];
        byte[] sendbuff = null;
        int flag = 0;
        int readcount = 0;
        boolean isStart = false;
        boolean isSendL2Over = false;
        ByteArrayInputStream inputStream 
                = new ByteArrayInputStream(bsl2msg.toByte());
        try {
            if (inputStream != null) {
                do {
                    if (readcount == 0) { // 第一次读流中的数据
                        flag = 0; //开始帧
                        isStart = true;
                    } else {
                        isStart = false;
                    }
                    readcount = inputStream.read(buffer, 0, 14);
                    if (readcount > 0 && readcount <= 14) {
                        sendbuff = new byte[readcount];
                        System.arraycopy(buffer, 0, sendbuff, 0, readcount);
                        if (inputStream.available() > 0) { //读取14个字符之后还有内容
                            if (readcount == 14 && !isStart) {
                                flag = 1; // 中间帧
                            }
                        } else {
                            flag = 2;//结束帧
                            inputStream.close();
                            isSendL2Over = true;
                        }
                        sendL1Msg(context, sendbuff, flag, isrepeat);
                    }
                } while (inputStream.available() > 0);
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e1) {
                SLog.e(TAG, e1);
            }
        }
        return isSendL2Over;     
    }
    
    public void sendL1Msg(Context context, byte[] buffer, int flag, boolean isrepeat) {
        BaseL1Message bsL1Msg = new BaseL1Message();
        String l1str = null;
        if (buffer != null && buffer.length > 0) {
            bsL1Msg.payload = new byte[buffer.length];
            System.arraycopy(buffer, 0, bsL1Msg.payload, 0, buffer.length);
            
            bsL1Msg.errFlag = (short) flag;
            bsL1Msg.isAiziBaseL1Head = true;
            bsL1Msg.isNeedAck = true;
            bsL1Msg.ackFlag = 0;
            bsL1Msg.version = 0;
            bsL1Msg.payloadLength = (short) buffer.length;
            if (isrepeat) {
                bsL1Msg.sequenceId = (short) mL2SquenceID;
            } else {
                if (mL2SquenceID > 65536) {
                    mL2SquenceID = 0;
                }
                bsL1Msg.sequenceId = (short) ++mL2SquenceID;
            }
            
            bsL1Msg.CRC16 = (short) CRC16.calcCrc16(bsL1Msg.payload);
            
            // 通过蓝牙传输数据
            BluetoothApi.getInstance(context).RecvEvent(new AsycEvent(bsL1Msg.tobyte()));
            
            byte[] bsl1buffer = bsL1Msg.tobyte();
            Intent intent = new Intent(Constant.DATA_TRANSFER_SEND);
            String l1payload = Utiliy.printHexString(bsl1buffer);
            intent.putExtra("transferdata", " L1 " + l1payload);
            EventBus.getDefault().post(intent); // 显示到测试界面上
            l1str = " L1 " + " SEND: " + l1payload + " isrepeat = " + isrepeat;
            Utiliy.logToFile(l1str); // 写入日志文件
            SLog.e(TAG, l1str);
        }
    }

    private boolean generateBaseL2MsgByteArray(BaseL1Message bsL1Msg) {
        boolean isover = false;
        try {
            if (bsL1Msg.isAiziBaseL1Head) {
                if (bsL1Msg.errFlag == 0) { // 标识起始位
                    clearL2RecvByte();
                    System.arraycopy(bsL1Msg.payload, 0, mL2RecvByte, 0, bsL1Msg.payloadLength);
                    mL2RecvCurrLen += bsL1Msg.payloadLength; 
                } else if (bsL1Msg.errFlag == 1) { // 标识中间位
                    if (mL2RecvCurrLen + bsL1Msg.payloadLength <= 128) {
                        System.arraycopy(bsL1Msg.payload, 0, mL2RecvByte, mL2RecvCurrLen, bsL1Msg.payloadLength);
                        mL2RecvCurrLen += bsL1Msg.payloadLength;
                        isover = false;
                    } else {
                        clearL2RecvByte();
                        isover = true;
                    }
                } else if (bsL1Msg.errFlag == 2) { // 标识结束位
                    if (mL2RecvCurrLen + bsL1Msg.payloadLength <= 128) {
                        System.arraycopy(bsL1Msg.payload, 0, mL2RecvByte, mL2RecvCurrLen, bsL1Msg.payloadLength);
                        mL2RecvCurrLen += bsL1Msg.payloadLength;
                    } else {
                        clearL2RecvByte();
                    }
                    isover = true;
                } else {
                    isover = false;
                }
            } else {
                isover = false;
            }
        } catch (Exception e) {
            isover = false;
            SLog.e(TAG, e);
        }  
        return isover;
    }



    private BaseL2Message getBaseL2Msg(byte[] l2data, int mL2RecvCurrLen2) {
        BaseL2Message bsl2Msg = new BaseL2Message();
        if (l2data.length > 1) {
            bsl2Msg.commanID = (short)(l2data[0] & 0xff); 
            bsl2Msg.versionCode = (short) ((l2data[1] & 0xf0) >> 4);
            //int len = l2data.length;
            bsl2Msg.payload = new byte[mL2RecvCurrLen2-2];
            System.arraycopy(l2data, 2, bsl2Msg.payload, 0, mL2RecvCurrLen2 - 2); // 拷贝l2数据
        }   
        return bsl2Msg;
    }

    private BaseL1Message getBaseL1Msg(byte[] basedata) {
        BaseL1Message bsL1Msg = new BaseL1Message();
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
