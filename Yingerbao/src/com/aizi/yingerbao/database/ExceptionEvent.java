package com.aizi.yingerbao.database;

import cn.bmob.v3.BmobObject;

public class ExceptionEvent extends BmobObject{

    public int mExceptionType = 0;
    public int mExceptionData1 = 0;
    public int mExceptionData2 = 0;
    public int mExceptionData3 = 0;
    
    public int mExceptionYear = 0;
    public int mExceptionMonth = 0;
    public int mExceptionDay = 0;
    public int mExceptionHour = 0;
    public int mExceptionMinute = 0;
    public int mExceptionSecond = 0;
}
