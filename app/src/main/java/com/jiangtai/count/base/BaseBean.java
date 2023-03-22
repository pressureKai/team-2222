package com.jiangtai.count.base;


import com.jiangtai.count.constant.Constant;

/**
 * Created by hecuncun on 2019/5/13
 */
public class BaseBean<T> {

    private static final String SUCCESSED_CODE= Constant.SUCCESSED_CODE;//请求成功 code=10001;

    private String error_code;
    private String error_msg;
    private T data;

    public String getCode() {
        return error_code;
    }

    public void setCode(String code) {
        this.error_code = code;
    }

    public String getMessage() {
        return error_msg;
    }

    public void setMessage(String message) {
        this.error_msg = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 判断请求是否成功
     *
     */
    public boolean isSuccessed(){
        return SUCCESSED_CODE.equals(error_code);
    }
}
