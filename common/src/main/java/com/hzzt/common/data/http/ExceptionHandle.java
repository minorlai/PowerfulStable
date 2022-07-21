package com.hzzt.common.data.http;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import com.hzzt.common.R;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;

import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.Utils;
import retrofit2.HttpException;


/**
 * @Author: Allen
 * @CreateDate: 2022/7/21
 * @Description:
 */
public class ExceptionHandle {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int SERVICE_UNAVAILABLE = 503;

    public static ResponseThrowable handleException(Throwable e) {
        ResponseThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseThrowable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED:
                    ex.message = Utils.getContext().getResources().getString(R.string.exp_error1);
                    break;
                case FORBIDDEN:
                    ex.message = Utils.getContext().getResources().getString(R.string.exp_error2);
                    break;
                case NOT_FOUND:
                    ex.message = Utils.getContext().getResources().getString(R.string.exp_error3);
                    break;
                case REQUEST_TIMEOUT:
                    ex.message = Utils.getContext().getResources().getString(R.string.exp_error4);
                    break;
                case INTERNAL_SERVER_ERROR:
                    ex.message = Utils.getContext().getResources().getString(R.string.exp_error5);
                    break;
                case SERVICE_UNAVAILABLE:
                    ex.message = Utils.getContext().getResources().getString(R.string.exp_error6);
                    break;
                default:
                    ex.message = Utils.getContext().getResources().getString(R.string.exp_error7);
                    break;
            }
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException || e instanceof MalformedJsonException) {
            ex = new ResponseThrowable(e, ERROR.PARSE_ERROR);
            ex.message = Utils.getContext().getResources().getString(R.string.exp_error8);
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ResponseThrowable(e, ERROR.NETWORD_ERROR);
            ex.message = Utils.getContext().getResources().getString(R.string.exp_error9);
            return ex;
        } else if (e instanceof javax.net.ssl.SSLException) {
            ex = new ResponseThrowable(e, ERROR.SSL_ERROR);
            ex.message = Utils.getContext().getResources().getString(R.string.exp_error10);
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ResponseThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = Utils.getContext().getResources().getString(R.string.exp_error11);
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ResponseThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = Utils.getContext().getResources().getString(R.string.exp_error11);
            return ex;
        } else if (e instanceof java.net.UnknownHostException) {
            ex = new ResponseThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = Utils.getContext().getResources().getString(R.string.exp_error12);
            return ex;
        } else {
            ex = new ResponseThrowable(e, ERROR.UNKNOWN);
            ex.message = Utils.getContext().getResources().getString(R.string.exp_error13);
            return ex;
        }
    }


    /**
     * 约定异常 这个具体规则需要与服务端或者领导商讨定义
     */
    class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 10031;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;
    }

}

