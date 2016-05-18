/*
 * Copyright (C) 2014 Baidu Inc. All rights reserved.
 */
package com.xzj.babyfun.deviceinterface;

/**
 * 路由器和server统一定义的error
 * @author panxu
 * @since 2014-5-20
 */
public final class ServerError {
    
    private ServerError() {
        
    }
    
    //与路由和server端保持一致的错误编码
    //参考： http://wiki.babel.baidu.com/twiki/bin/view/Com/CloudOS/ErrorCode
    /**请求参数错误*/
    public static final int ERR_TALK_TO_SERVICE_FAIL = 1017;
    /**请求参数错误*/
    public static final int ERR_PARAM = 1006;
    public static final int ERR_PARAM_SERVER = 39;
    /**绑定关系失效*/
    public static final int ERR_BIND_INALIDATE = 1007;
    /**固件升级失败*/
    public static final int ERR_FAIL_UPGRADE_ROUTER = 1020;
    /**在远程下载的时候的绑定关系失效的错误错误*/
    public static final int ERR_BIND_INVALIDATE_1 = 5;
    
    /**这个错误是通用的错误 绑定关系失效*/
    public static final int ERR_BIND_INVALIDATE_2 = 49;
    
    /**远程下载时候绑定错误码*/
    public static final int ERR_BIND_INDALIDATE_3 = 55;
    /**bduss15天失效*/
    public static final int ERR_BDUSS_INVALIDATE = 56;
    /**百度账号-tpl(产品线)错误*/
    public static final int ERR_ACCOUNT_TPL = 51;
    /**bduss有效期超过15天*/
    public static final int ERR_BDUSST_TIMEOUT = 52;
    
    
    
    /**已经绑定 */
    public static final int ERR_ALREADY_BIND_BAIDU = 54;
  
    
    
    public static final String ERR_BIND_INVALIDATE_MSG = "not bind";
    
    /** bduss失效 */
    public static final int ERR_SESSION_INVALID = 1;
    public static final String ERR_SESSION_INVALID_MSG = "session invalid";
    
 
    /**新的接口中返回的错误*/
    public static final int ERR_ADMIN_SIGN_INVALID = 1008;
    public static final String ERR_ADMIN_SIGN_INVALID_MSG = "Admin密码错误";
    
    public static final int ERR_BIND1_ADMIN_SIGN_INVALID = 2;
    public static final String ERR_BIND1_ADMIN_SIGN_INVALID_MSG = "Bind1 Admin密码错误";
    
    public static final int ERR_DEVICE_BINDED_BY_ANOTHER = 50;
    public static final String ERR_DEVICE_BINDED_BY_ANOTHER_MSG = "Device Binded by another.";
    
    /**绑定错误*/
    public static final int ERR_REMOTE_DOWNLOAD_DISK_SPACE_NOT_ENOUGH = 2002;
    public static final int ERR_REMOTE_DOWNLOAD_DISK_READ_ONLY = 2003;
    
    /**远程下载**/
    public static final int ERR_NO_DISK = 2013;
}
