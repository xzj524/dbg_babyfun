package com.aizi.yingerbao.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;

import com.aizi.yingerbao.baseheader.BaseL1Message;
import com.aizi.yingerbao.baseheader.BaseL2Message;
import com.aizi.yingerbao.baseheader.KeyPayload;
import com.aizi.yingerbao.bluttooth.BluetoothApi;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.crc.CRC16;
import com.aizi.yingerbao.eventbus.AsycEvent;
import com.aizi.yingerbao.logging.SLog;

import de.greenrobot.event.EventBus;

public class BaseMessageHandler {
    
    private static final String TAG = BaseMessageHandler.class.getSimpleName();
    private final static short BASE_DATA_HEAD = 6;
    private final static short BASE_L2_DATA_LEN = 5;
    private static Object mYingerbaoLock = new Object();
    
    public static boolean mIsReceOver = true;
    public static ByteArrayOutputStream mL2OutputStream = new ByteArrayOutputStream();
    public static boolean isWriteSuccess = true;
    public static int repeattime = 0;
    static byte[] baseTwobytes;
    public static int squenceID = 1;
    public static int mL1squenceid = -1;
    private static final long WAIT_PERIOD = 5 * 1000; //10 seconds
    static Timer mTimer;  
    static ExecutorService mBaseExecutorService = Executors.newCachedThreadPool();
    static PendingIntent mPendingIntent;
    
    public static void acquireBaseData(Context context, BluetoothDevice bluetoothDevice, 
            BluetoothGattCharacteristic characteristic) {
        try {
            if (Constant.BLE_UUID_NUS_RX_CHARACTERISTIC.equals(characteristic.getUuid())) {
                byte[] baseData = characteristic.getValue();
                if (baseData == null) {
                    return;
                }
                
                if (baseData.length >= BASE_DATA_HEAD) {
                    BaseL1Message bsL1Msg = getBaseL1Msg(baseData); // 生成L1数据
                    
                    String l1payload = Utiliy.printHexString(bsL1Msg.tobyte());
                    SLog.e(TAG, "recv l1 msg  = " + l1payload);
                    
                    if (bsL1Msg.ackFlag == 1) { //收到设备的ack信息
                        SLog.e(TAG, "receive ACK");
                        Utiliy.logToFile(" L1 " + " RECV ACK: " + l1payload); // 写入日志文件
                        Intent intent = new Intent(Constant.DATA_TRANSFER_RECEIVE);
                        intent.putExtra("transferdata", " L1 " + " ACK: " + l1payload);
                        EventBus.getDefault().post(intent); // 显示到测试界面上
                    } else {
                        //收到设备发过来的信息，需要返回ACK
                        if (bsL1Msg.isNeedAck && bsL1Msg.ackFlag == 0) { 
                            int crc16 = CRC16.calcCrc16(bsL1Msg.payload);
                            if (bsL1Msg.CRC16 == (short)crc16) {
                                sendACKBaseL1Msg(context, baseData);

                                if (bsL1Msg.errFlag == 2) { // 标识结束位
                                    mL1squenceid = -1;
                                } else if (bsL1Msg.errFlag == 1) { // 标识中间位
                                    if (bsL1Msg.sequenceId - mL1squenceid != 1) {
                                        if (mL2OutputStream != null) {
                                            mL2OutputStream.reset();
                                            mL2OutputStream.close();  
                                        }
                                        return;//中间位与上一个相差不为1 则抛弃
                                    } else {
                                        mL1squenceid = bsL1Msg.sequenceId;
                                    }
                                } else if (bsL1Msg.errFlag == 0) { // 标识起始位
                                    mL1squenceid = bsL1Msg.sequenceId;
                                }
                                
                                // 设置超时机制，超时后所有接收的l1层数据清空
                                if (mPendingIntent != null) {
                                    Utiliy.cancelAlarmPdIntent(context, mPendingIntent);
                                }
                                mPendingIntent = Utiliy.getDelayPendingIntent(context, Constant.ALARM_WAIT_L1);
                                Utiliy.setDelayAlarm(context, WAIT_PERIOD, mPendingIntent);

                                handleL1Msg(context, bsL1Msg);
                                
                                Utiliy.logToFile(" L1 " + " RECV DATA: " + l1payload);
                                Intent intent = new Intent(Constant.DATA_TRANSFER_RECEIVE);
                                intent.putExtra("transferdata", " L1 " + " DATA: " + l1payload);
                                EventBus.getDefault().post(intent); // 显示到测试界面上
                              }                   
                            } else {
                                SLog.e(TAG, "Not Send ACK");
                            }
                        }  
                    } 
                }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        
    }

    private static void handleL1Msg(Context context, BaseL1Message bsL1Msg) {
        try {
            mIsReceOver = generateBaseL2MsgByteArray(bsL1Msg); // 生成L2所需要的byte数组
            if (mIsReceOver) {
                if (mL2OutputStream.size() > 0) {
                    byte[] l2buff = mL2OutputStream.toByteArray();
                    int l2len = ((l2buff[3] & 0x01) << 8) | (l2buff[4] & 0xff);
                    SLog.e(TAG, "receiveL2DATA before l2len = " + l2len + " totallen = " + l2buff.length);
                    if (l2len + BASE_L2_DATA_LEN == l2buff.length) {
                        SLog.e(TAG, "receive L2 DATA");
                        BaseL2Message bsl2Msg = getBaseL2Msg(mL2OutputStream.toByteArray()); 
                        mL2OutputStream.reset();
                        
                        MessageParse.getInstance(context).RecvBaseL2Msg(bsl2Msg);
                    }
                }            
            } else {
                SLog.e(TAG, "reflectTranDataType 1");
                Utiliy.reflectTranDataType(1);
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }
    
    
    static Runnable recvtimeoutRunnable = new Runnable() {
        
        @Override
        public void run() {
            try {
                Thread.sleep(WAIT_PERIOD);
                if (mL2OutputStream != null) {
                    mL2OutputStream.reset();
                    mL2OutputStream.close();  
                }
            } catch (Exception e) {
                SLog.e(TAG, e);
            }
        }
    };
    
/*    static AZRunnable timeOutRunnable = new AZRunnable("sendtimeOutRunnable", AZRunnable.RUNNABLE_TIMER) {
        @Override
        public void brun() {
            try {
                Thread.sleep(SLEEP_TIME);
                synchronized (synchronizedLock) {
                    synchronizedLock.notifyAll();
                    SLog.e(TAG, "CommandCenter mCommandSendRequest  time out notifyALL");
                }
            } catch (Exception e) {
                SLog.e(TAG, e);
            }
        }
    };*/
    
    static TimerTask task = new TimerTask(){    
             public void run(){    
                 try {
                     if (!mIsReceOver) {
                         mIsReceOver = true;
                         mL1squenceid = -1;
                         if (mL2OutputStream != null) {
                             mL2OutputStream.reset();
                             mL2OutputStream.close();  
                         }
                         SLog.e(TAG, "delay task is running###############");
                     }
                } catch (Exception e) {
                    SLog.e(TAG, e);
                }   
             }    
         };    

    private static void sendACKBaseL1Msg(Context context, byte[] baseData) {
        byte[] ACKMsg = new byte[6];
        System.arraycopy(baseData, 0, ACKMsg, 0, 6);
        ACKMsg[1] = (byte) ((baseData[1] | 0x10) & 0x10);
        ACKMsg[2] = (byte) (baseData[2] & 0);
        ACKMsg[4] = (byte) (baseData[4] & 0);
        ACKMsg[5] = (byte) (baseData[5] & 0);
        
        String l1ack = Utiliy.printHexString(ACKMsg);
        Utiliy.logToFile(" L1  SEND ACK: " + l1ack); // 写入日志文件
        SLog.e(TAG, " L1 SEND ACK: " + l1ack);
        Intent intent = new Intent(Constant.DATA_TRANSFER_SEND);
        intent.putExtra("transferdata", " L1 " + " ACK: " + l1ack);
        EventBus.getDefault().post(intent); // 显示到测试界面上
        
        BluetoothApi.getInstance(context).RecvEvent(new AsycEvent(ACKMsg));
    }
    
    public static boolean sendL2Message(Context context, BaseL2Message bsl2msg) {
        boolean isSendL2Over = false;
        synchronized (mYingerbaoLock) {
            try {
                sendL2Msg(context, bsl2msg);
                
                /****在测试界面显示出来,写入日志文件*****/
                String l2payload = Utiliy.printHexString(bsl2msg.toByte());
                SLog.e(TAG, "HEX Send string l2load1 = " + l2payload);
                
                Intent intent = new Intent(Constant.DATA_TRANSFER_SEND);
                intent.putExtra("transferdata", "L2 " + l2payload);
                EventBus.getDefault().post(intent);
                Utiliy.logToFile(" L2 " + " SEND: " + l2payload); 
                /****在测试界面显示出来,写入日志文件*****/

            } catch (Exception e) {
                SLog.e(TAG, e);
            }
        }
        return isSendL2Over;
    }
    
    private static boolean sendL2Msg(Context context, BaseL2Message bsl2msg) {
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
                        sendL1Msg(context, sendbuff, flag);
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

    
    
    public static void sendL1Msg(Context context, byte[] buffer, int flag) {
        BaseL1Message bsL1Msg = new BaseL1Message();
        if (buffer != null && buffer.length > 0) {
            bsL1Msg.payload = new byte[buffer.length];
            System.arraycopy(buffer, 0, bsL1Msg.payload, 0, buffer.length);
            
            bsL1Msg.errFlag = (short) flag;
            bsL1Msg.isAiziBaseL1Head = true;
            bsL1Msg.isNeedAck = true;
            bsL1Msg.ackFlag = 0;
            bsL1Msg.version = 0;
            bsL1Msg.payloadLength = (short) buffer.length;
            if (squenceID > 65536) {
                squenceID = 0;
            }
            bsL1Msg.sequenceId = (short) ++squenceID;
            bsL1Msg.CRC16 = (short) CRC16.calcCrc16(bsL1Msg.payload);
            
            // 通过蓝牙传输数据
            BluetoothApi.getInstance(context).RecvEvent(new AsycEvent(bsL1Msg.tobyte()));
            
            byte[] bsl1buffer = bsL1Msg.tobyte();
            Intent intent = new Intent(Constant.DATA_TRANSFER_SEND);
            String l1payload = Utiliy.printHexString(bsl1buffer);
            intent.putExtra("transferdata", " L1 " + l1payload);
            EventBus.getDefault().post(intent); // 显示到测试界面上
            Utiliy.logToFile(" L1 " + " SEND: " + l1payload); // 写入日志文件
            SLog.e(TAG, " L1 " + " SEND: " + l1payload);
        }
    }

    private static boolean generateBaseL2MsgByteArray(BaseL1Message bsL1Msg) {
        boolean isover = false;
        int index = 0;
        try {
            if (bsL1Msg.isAiziBaseL1Head) {
                if (bsL1Msg.errFlag == 0) { // 标识起始位
                    index = bsL1Msg.sequenceId;
                    mL2OutputStream.reset();
                    mL2OutputStream.write(bsL1Msg.payload);
                } else if (bsL1Msg.errFlag == 1) { // 标识中间位
                    mL2OutputStream.write(bsL1Msg.payload);
                } else if (bsL1Msg.errFlag == 2) { // 标识结束位
                    isover = true;
                    mL2OutputStream.write(bsL1Msg.payload);
                } else {
                    isover = false;
                }
                mL2OutputStream.close();
            } else {
                isover = false;
            }
        } catch (Exception e) {
            isover = false;
            SLog.e(TAG, e);
        }  
        return isover;
    }

    public static BaseL2Message generateBaseL2Msg(short commanid, short version, 
            KeyPayload keyPayload){
        BaseL2Message bsl2Msg = new BaseL2Message();
        try {
            bsl2Msg.commanID = commanid;
            bsl2Msg.versionCode = version;
            bsl2Msg.payload = new byte[keyPayload.keyLen + 3];
            System.arraycopy(keyPayload.toByte(), 0, bsl2Msg.payload, 0, keyPayload.keyLen + 3);
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        return bsl2Msg;
    }

    private static BaseL2Message getBaseL2Msg(byte[] l2data) {
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
