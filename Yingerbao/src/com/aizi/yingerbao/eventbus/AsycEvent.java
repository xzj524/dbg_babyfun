package com.aizi.yingerbao.eventbus;

public class AsycEvent {
      byte[] basedata;
      public boolean mIsWait;
      public boolean isAck = false;
      public AsycEvent(byte[] bsdata){
          basedata = bsdata;
          mIsWait = true;
      }
      
      public AsycEvent(byte[] bsdata, boolean iswait){
          basedata = bsdata;
          mIsWait = iswait;
      }
      
      public byte[] getByte(){  
          return basedata;  
      } 
      
      public void clearByte() {
        basedata = null;
    }
}
