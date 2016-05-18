package com.xzj.babyfun.deviceinterface;

/**
 * 接口response
 * 
 * @author xuzejun
 * 
 * @param <T>
 */
public class DeviceResponse<T> {
    /** response 结果 */
    public T result;
    /** 返回错误信息 */
    public DeviceError error;

    /**
     * 生成正确的返回结果
     * 
     * @param <T>
     *            泛化声明
     * @param result
     *            返回结果
     * @return 生成返回对象
     */
    public static <T> DeviceResponse<T> success(T result) {
        return new DeviceResponse<T>(result, null);
    }

    /**
     * 生成错误返回信息对象
     * 
     * @param <T>
     *            泛化声明
     * @param error
     *            错误信息
     * @return 生成错误返回信息对象
     */
    public static <T> DeviceResponse<T> error(DeviceError error) {
        return new DeviceResponse<T>(null, error);
    }

    /**
     * response 对象
     * 
     * @param ret
     *            返回结果
     * @param err
     *            错误信息
     */
    public DeviceResponse(T ret, DeviceError err) {
        this.result = ret;
        this.error = err;
    }

    /**
     * 
     */
    public DeviceResponse() {
    }
}
