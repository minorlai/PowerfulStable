package com.hzzt.common.entity.req;

/**
 * @Author: Allen
 * @CreateDate: 2022/7/21
 * @Description:
 */
public class CommonUI {
    public int _code;
    public int errorCode;
    public int _type;
    public String _tag;
    public Object _obj;

    public CommonUI() {
    }

    public CommonUI(int _code, int _type, String _tag) {
        this._code = _code;
        this._type = _type;
        this._tag = _tag;
    }

    public CommonUI(int _code, int _type, String _tag, Object _obj) {
        this._code = _code;
        this._type = _type;
        this._tag = _tag;
        this._obj = _obj;
    }

    public CommonUI(int _code, int errorCode, int _type, String _tag, Object _obj) {
        this._code = _code;
        this.errorCode = errorCode;
        this._type = _type;
        this._tag = _tag;
        this._obj = _obj;
    }
}
