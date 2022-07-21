package com.hzzt.common.entity.resp;


/**
 * @Author: Allen
 * @CreateDate: 2021/6/9
 * @Description: 响应二次封装
 */
public class CommonResponse<T>{
    private int code;
    private int msgCode;
    private int errorCode;
    private String msg;
    private String tipsMsg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public boolean isOk() {
        return code == 0;
    }

    public int getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(int msgCode) {
        this.msgCode = msgCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTipsMsg() {
        return tipsMsg;
    }

    public void setTipsMsg(String tipsMsg) {
        this.tipsMsg = tipsMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int get_type() {
        return _type;
    }

    public void set_type(int _type) {
        this._type = _type;
    }

    //请求响应类型，根据类型区分接口
    public int _type;
    public CommonResponse type(int _type) {
        this._type = _type;
        return this;
    }
    public CommonResponse code(int code) {
        setCode(code);
        return this;
    }
}
