package com.aizi.yingerbao.command;

public class CommandReply {

    private int replyCode;

    private byte[] replyData;

    public int getReplyCode() {
        return replyCode;
    }

    public void setReplyCode(int errorCode) {
        replyCode = errorCode;
    }

    public byte[] getReplyData() {
        return replyData;
    }

    public void setReplyData(byte[] resultData) {
        replyData = resultData;
    }
}
