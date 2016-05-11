package com.xzj.babyfun.eventbus;

public class AsycEvent {
      byte[] basedata;
      public AsycEvent(byte[] bsdata){
          basedata = bsdata;
      }
      
      public byte[] getByte(){  
          return basedata;  
      } 
}
