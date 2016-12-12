package com.aizi.yingerbao.eventbus;

public class AsycEvent {
      byte[] basedata;
      public boolean isAck = false;
      public AsycEvent(byte[] bsdata){
          basedata = bsdata;
      }
      
      public byte[] getByte(){  
          return basedata;  
      } 
}
