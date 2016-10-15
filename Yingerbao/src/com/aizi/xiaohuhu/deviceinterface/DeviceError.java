package com.aizi.xiaohuhu.deviceinterface;

/**
 * 错误消息定义
 * 
 * @author xuzejun
 * 
 */
public class DeviceError {
    /** 错误信息code */
    public final int errorCode;
    /** 错误信息desc */
    public final String errorMsg;

    /**
     * 构造函数
     * 
     * @param errorCode
     *            错误信息code
     * @param errorMsg
     *            错误信息desc
     */
    public DeviceError(final int errorCode, final String errorMsg) {
        
        switch (errorCode) {
        case ServerError.ERR_ADMIN_SIGN_INVALID:
            
            this.errorCode = SDK_ERR_ADMIN_SIGN_INVALIDATE;
            this.errorMsg = SDK_ERR_ADMIN_SIGN_INVALIDATE_MSG;
            break;

        case ServerError.ERR_BIND_INALIDATE:
        case ServerError.ERR_BIND_INVALIDATE_1:
        case ServerError.ERR_BIND_INVALIDATE_2:
        case ServerError.ERR_BIND_INDALIDATE_3:

            this.errorCode = SDK_ERR_BIND_INVALIDATE;
            this.errorMsg = SDK_ERR_BIND_INVALIDATE_MSG;
            break;
        
        case ServerError.ERR_BDUSS_INVALIDATE:
        case ServerError.ERR_SESSION_INVALID:
        case ServerError.ERR_ACCOUNT_TPL:
        case ServerError.ERR_BDUSST_TIMEOUT:
            this.errorCode = SDK_ERR_BDUSS_INVALIDATE;
            this.errorMsg = SDK_ERR_BDUSS_INVALIDATE_MSG;
            break;
        case ServerError.ERR_PARAM:
        case ServerError.ERR_PARAM_SERVER:
            this.errorCode = SDK_ERR_PARAM_INVALIDATE;
            this.errorMsg = SDK_ERR_PARAM_INVALIDATE_MSG;
            break;
        case ServerError.ERR_TALK_TO_SERVICE_FAIL:
        	this.errorCode = SDK_ERR_TALK_FAIL;
            this.errorMsg = SDK_ERR_TALK_FAIL_MSG;
            break;
            
        case ServerError.ERR_DEVICE_BINDED_BY_ANOTHER:
            this.errorCode = SDK_ERR_DEVICE_BIND_BY_ANOTHER;
            this.errorMsg = SDK_ERR_DEVICE_BIND_BY_ANOTHER_MSG;
            break;

            
        case ServerError.ERR_REMOTE_DOWNLOAD_DISK_READ_ONLY:
            this.errorCode = SDK_ERR_DOWNLOAD_ERR_FOR_DISK_READ_ONLY;
            this.errorMsg = SDK_ERR_DOWNLOAD_ERR_FOR_DISK_READ_ONLY_MSG;
            break;
            
        case ServerError.ERR_REMOTE_DOWNLOAD_DISK_SPACE_NOT_ENOUGH:
            this.errorCode = SDK_ERR_DOWNLOAD_ERR_FOR_DISK_SAPCE_NOT_ENOUGH;
            this.errorMsg = SDK_ERR_DOWNLOAD_ERR_FOR_SAPCE_NOT_ENOUGH_MSG;
            break;
        case ServerError.ERR_NO_DISK:
            this.errorCode = SDK_ERR_NO_DISK;
            this.errorMsg = SDK_ERR_NO_DISK_MSG;
            break;
        case ServerError.ERR_FAIL_UPGRADE_ROUTER:
            this.errorCode = SDK_ERR_FAIL_UPGRADE_ROUTER;
            this.errorMsg = SDK_ERR_FAIL_UPGRADE_ROUTER_MSG;
            break;
        default:
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
            break;
        }
        
    }
    
    @Override
    public String toString() {
        return "error:(" + errorCode + "," + errorMsg + ")";
    }
    
    //SDK判定的错误从-1开始定义 避免与路由器和server的产生重复
    
    /** 参数错误 */
    public static final int SDK_OPERATION_NO_ERR           = 200;
    public static final String SDK_OPERATION_NO_ERR_MSG = "http  no error";
    
    /** 参数错误 */
    public static final int SDK_ERR_PARAM_INVALIDATE           = -1;
    public static final String SDK_ERR_PARAM_INVALIDATE_MSG = "parameter invalid";
    
    /**与路由器不再同一个局域网*/
    public static final int SDK_ERR_NOT_IN_LOCAL            = -2;
    public static final String SDK_ERR_NOT_IN_LOCAL_MSG     = "with router in different network";
    
    /**bduss失效*/
    public static final int SDK_ERR_BDUSS_INVALIDATE        = -3;
    public static final String SDK_ERR_BDUSS_INVALIDATE_MSG = "bduss invalidate";
    
    /**百度账号未登录*/
    public static final int SDK_ERR_BAIDU_ACCOUNT_NOT_LOGIN  = -4;
    public static final String SDK_ERR_BAIDU_ACCOUNT_NOT_LOGIN_MSG = "baidu account not login";
    
    /**admin sign失效*/
    public static final int SDK_ERR_ADMIN_SIGN_INVALIDATE   = -5;
    public static final String SDK_ERR_ADMIN_SIGN_INVALIDATE_MSG = "admin sign invalidate";
    
    
    /**admin not login*/
    public static final int SDK_ERR_ADMIN_NOT_LOGIN         = -6;
    public static final String SDK_ERR_ADMIN_NOT_LOGIN_MSG = "admin not login";
    
    /**绑定关系失效*/
    public static final int SDK_ERR_BIND_INVALIDATE         = -7;
    public static final String SDK_ERR_BIND_INVALIDATE_MSG = "bind invalidate";
    

    /**与服务对话失败*/
    public static final int SDK_ERR_TALK_FAIL         = -8;
    public static final String SDK_ERR_TALK_FAIL_MSG = "talk to service failed";
    
    public static final int SDK_ERR_DEVICE_BIND_BY_ANOTHER = -9;
    public static final String SDK_ERR_DEVICE_BIND_BY_ANOTHER_MSG = "Device is binded by another.";

    
    public static final int SDK_ERR_APP_NOT_MATCH_ROM_VERSION_CODE = -10;
    public static final String SDK_ERR_APP_NOT_MATCH_ROM_VERSION_CODE_MSG = "app version not match rom";
    
    public static final int SDK_ERR_APP_NOT_MATCH_ROM_VERSION_TYPE = -11;
    public static final String SDK_ERR_APP_NOT_MATCH_ROM_VERSION_TYPE_MSG = "app version type not match rom";

    public static final int SDK_ERR_JSON_INVALIDATE 		= -20;
    public static final String SDK_ERR_JSON_INVALIDATE_MSG = "JSON invalidate";
    
    /**下载*/
    public static final int SDK_ERR_DOWNLOAD_ERR_FOR_DISK_READ_ONLY = -30;
    public static final String SDK_ERR_DOWNLOAD_ERR_FOR_DISK_READ_ONLY_MSG = "disk read only";
    
    public static final int SDK_ERR_DOWNLOAD_ERR_FOR_DISK_SAPCE_NOT_ENOUGH = -31;
    public static final String SDK_ERR_DOWNLOAD_ERR_FOR_SAPCE_NOT_ENOUGH_MSG = "disk is full";

    
    
    /*--------------------------访问server的HTTP连接错误------------------------------*/
    /**Server error*/
    public static final int SDK_ERR_HTTP_SERVER_EXCEPTION   = -50;
    public static final String SDK_ERR_HTTP_SERVER_EXCEPTION_MSG = "Server Exception";
    
    /**http 请求IO 异常*/
    public static final int SDK_ERR_HTTP_IO_EXCEPTION       = -51;
    public static final String SDK_ERR_HTTP_IO_EXCEPTION_MSG = "IO Exception";
    
    /**请求超时*/
    public static final int SDK_ERR_HTTP_TIME_OUT           = -52;
    public static final String SDK_ERR_HTTP_TIME_OUT_MSG = "socket time out";

    /**PUT 文件未找到文件*/
    public static final int SDK_ERR_PUT_FILE_NOT_FOUNT  = -53;
    public static final String SDK_ERR_PUT_FILE_NOT_FOUNT_MSG = "http put file not found";

   /* *//**统一的http错误*//*
    public static final int SDK_ERR_HTTP                    = -53;
    public static final String SDK_ERR_HTTP_MSG         = "http exception";*/
    
    /*--------------------------------------------------------------------------*/

    /** 未知错误 */
    public static final int SDK_ERR_DEFAULT = -10000;
    public static final String ERR_DEFAULT_MSG = "unknown error";
    
    public static final int SDK_MAX_ERROR = -20000;
    
    /**Qos正在设置中*/
    public static final int SET_QOS_IN_PROGRESS           = 1021;
    public static final String SET_QOS_IN_PROGRESS_MSG = "set qos is in progress";
    
    public static final int QOS_MEASURE_IN_PROGRESS           = 1023;

    /**远程下载*/
    public static final int SDK_ERR_NO_DISK           = -1000;
    public static final String SDK_ERR_NO_DISK_MSG           = "no disk";
    
    /**路由发出消息固件升级失败*/
    public static final int SDK_ERR_FAIL_UPGRADE_ROUTER           = -1100;
    public static final String SDK_ERR_FAIL_UPGRADE_ROUTER_MSG           = "路由发出消息固件升级失败";
    
    /**静态IP配置错误*/
    public static final int SDK_ERR_STATIC_IP           = 1101;
    public static final String SDK_ERR_STATIC_IP_MSG           = "config wan ip error";
   
    /**网关配置错误*/
    public static final int SDK_ERR_STATIC_IP_GATE           = 1102;
    public static final String SDK_ERR_STATIC_IP_GATE_MSG           = "config wan ip gate error";
}
