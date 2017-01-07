package com.aizi.yingerbao.utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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

public class SendMessageHandler {
    
    private static final String TAG = SendMessageHandler.class.getSimpleName();

    private static SendMessageHandler mInstance;
    Context mContext;
    private static Object mSendMessageLock = new Object();
    public static int mL2SquenceID = 1;
    
    public SendMessageHandler(Context context) {
        mContext = context;
    }
    
    public static SendMessageHandler getInstance(Context context) {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new SendMessageHandler(context);
            return mInstance;
        }
    }
    
    public synchronized boolean sendL2Message(Context context, BaseL2Message bsl2msg, 
            boolean isrepeat, boolean mIsWaitResult) {
        boolean isSendL2Over = false;
        String l2str = null;
        synchronized (mSendMessageLock) {
            try {
                String l2payload = Utiliy.printHexString(bsl2msg.toByte());
                l2str = "L2 SEND: " + l2payload + " repeat = " + isrepeat;
                SLog.e(TAG, l2str);
                Utiliy.logToFile(l2str); 
                sendL2Msg(context, bsl2msg, isrepeat, mIsWaitResult);
                
                /****在测试界面显示出来,写入日志文件*****/
                

                Intent intent = new Intent(Constant.DATA_TRANSFER_SEND);
                intent.putExtra("transferdata", "L2 " + l2payload);
                EventBus.getDefault().post(intent);

               
                
               
                /****在测试界面显示出来,写入日志文件*****/
                
               

            } catch (Exception e) {
                SLog.e(TAG, e);
            }
        }
        return isSendL2Over;
    }
    
    
    private synchronized boolean sendL2Msg(Context context, BaseL2Message bsl2msg,
            boolean isrepeat, boolean mIsWaitResult) {
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
                        sendL1Msg(context, sendbuff, flag, isrepeat, mIsWaitResult);
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
    
    public synchronized void sendL1Msg(Context context, byte[] buffer, int flag,
            boolean isrepeat, boolean mIsWaitResult) {
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
            
            byte[] bsl1buffer = bsL1Msg.tobyte();
            String l1payload = Utiliy.printHexString(bsl1buffer);
            l1str = " L1 " + " SEND: " + l1payload + " isrepeat = " + isrepeat;
            SLog.e(TAG, l1str);
            Utiliy.logToFile(l1str); // 写入日志文件
            // 通过蓝牙传输数据
            BluetoothApi.getInstance(context).RecvEvent(new AsycEvent(bsL1Msg.tobyte(), mIsWaitResult));
            
            Intent intent = new Intent(Constant.DATA_TRANSFER_SEND);
            intent.putExtra("transferdata", " L1 " + l1payload);
            EventBus.getDefault().post(intent); // 显示到测试界面上    
        }
    }
}
