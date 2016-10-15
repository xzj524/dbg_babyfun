package com.aizi.yingerbao.eventbus;

public class AsycEvent {
      byte[] basedata;
      public AsycEvent(byte[] bsdata){
          basedata = bsdata;
      }
      
      public byte[] getByte(){  
          return basedata;  
      } 
}
