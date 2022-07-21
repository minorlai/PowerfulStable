package com.hzzt.powerful.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;


import com.hzzt.common.data.http.HttpDataSourceImpl;
import com.hzzt.common.entity.req.CommonUI;
import com.hzzt.common.entity.resp.CommonResponse;
import com.hzzt.common.utils.MainUtil;
import com.hzzt.common.utils.RxHttpUtil;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.NetworkUtil;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author: Allen
 * @date: 2021/6/9
 * @description: VM 二次封装
 */
public abstract class BaseVM extends BaseViewModel<HttpDataSourceImpl> {
    public int actType;
    //上下文
    public static Context mContext;
    //订阅UI更新事件
    public SingleLiveEvent<CommonUI> updateEvent = new SingleLiveEvent<>();
    //订阅请求事件
    public SingleLiveEvent<CommonResponse> responseEvent = new SingleLiveEvent<>();
    //事件集合
    private List<Disposable> rxBusList = new ArrayList<>();
    public Handler handler;

    public BaseVM(@NonNull Application application) {
        super(application);
    }

    public BaseVM(@NonNull Application application, HttpDataSourceImpl model) {
        super(application, model);
    }

    //请求初始化类型
    public abstract void initActType(int actType);

    public static void initContext(Context context) {
        mContext = context;
    }

    @Override
    public void registerRxBus() {
        super.registerRxBus();
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        for (Disposable disposable : rxBusList) {
            RxSubscriptions.remove(disposable);
        }
        rxBusList.clear();
    }

    @SuppressLint("CheckResult")
    public void registerBus(Class cls, Consumer consumer) {
        Disposable disposable = RxBus.getDefault().toObservable(cls.getClass())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        RxSubscriptions.add(disposable);
        rxBusList.add(disposable);
    }

    //发送bus内容
    public void sendBus(Object obj) {
        RxBus.getDefault().post(obj);
    }

    //bus Sticky
    public void sendStickyBus(Object obj) {
        RxBus.getDefault().postSticky(obj);
    }

    //获取string
    public String getResToStr(int res, int value) {
        if (mContext == null) {
            return "";
        }
        return String.format(mContext.getResources().getString(res), value);
    }

    //获取string
    public String getResToStr(int res) {
        if (mContext == null) {
            return "";
        }
        return mContext.getResources().getString(res);
    }

    public String getResToStr(int res, String value) {
        if (mContext == null) {
            return "";
        }
        return String.format(mContext.getResources().getString(res), value);
    }

    /**
     * 显示吐司
     *
     * @param resTxt
     */
    public void showMsg(String resTxt) {
        ToastUtils.showShort(resTxt);
    }

    public void showMsg(int resText) {
        ToastUtils.showShort(getResToStr(resText));
    }

    //String format 字符串
    public void showMsg(int res, String value) {
        ToastUtils.showShort(String.format(getResToStr(res), value));
    }

    //String format 整型
    public void showMsg(int res, int value) {
        ToastUtils.showShort(String.format(getResToStr(res), value));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        removeRxBus();
    }

    @SuppressLint("HandlerLeak")
    public void initHandler() {
        if (handler != null) return;
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
            }
        };
    }

    public void handleMsg(Message msg) {

    }

    //网络请求
    public <T> void addRequest(final int type, final boolean showLoading, final boolean showError, Observable request) {
        if (!NetworkUtil.isNetworkAvailable(getApplication())) {  //网络不行
            responseEvent.setValue(new CommonResponse().type(type).code(-101));
            updateEvent.setValue(new CommonUI(101, type, MainUtil.SERVER_RESPONSE_TAG));
            return;
        }
        //添加请求队列
        addSubscribe(request
                .compose(RxHttpUtil.schedulersTransformer())
               .compose(RxHttpUtil.exceptionTransformer())
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> {
                    if (showLoading) {  //开始请求
                        updateEvent.setValue(new CommonUI(104, type, MainUtil.SERVER_RESPONSE_TAG));
                    }
                })
                .doFinally(() -> {
                    if (showLoading) {  //请求结束
                        updateEvent.setValue(new CommonUI(105, type, MainUtil.SERVER_RESPONSE_TAG));
                    }
                })
                .subscribe((Consumer<CommonResponse<T>>) response -> {
                    //成功
                    if (!response.isOk() && showError) {
                            updateEvent.setValue(new CommonUI(response.getCode(),response.getErrorCode(), type, MainUtil.SERVER_RESPONSE_TAG, response.getMsg()));
                    }
                    //请求结果抛出
                    responseEvent.setValue(response.type(type));
                }, throwable -> {
                    //异常
                    CommonResponse commonResponse = new CommonResponse();
//                    if (throwable instanceof ResponseThrowable) {
//                        responseEvent.setValue(commonResponse.type(type).code(ExceptionHandler.Companion.getErrorCode()));
//                        updateEvent.setValue(new CommonUI(ExceptionHandler.Companion.getErrorCode(), type, MainUtil.SERVER_RESPONSE_TAG, ExceptionHandler.Companion.getErrorMsg()));
//                    }
                    if (throwable instanceof ResponseThrowable) {
                        responseEvent.setValue(commonResponse.type(type).code(((ResponseThrowable) throwable).code));
                        updateEvent.setValue(new CommonUI(((ResponseThrowable) throwable).code, type, MainUtil.SERVER_RESPONSE_TAG, ((ResponseThrowable) throwable).message));
                    }

                }, () -> KLog.d("完成-->")));
    }



    //文件上传
    public static RequestBody buidUploadBody(Map<String, String> pathMap) {
        if (pathMap == null && pathMap.size() == 0) return null;
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : pathMap.entrySet()) {
            File file = new File(entry.getValue());
            if (!file.exists()) return null;
            builder.addFormDataPart(entry.getKey(), URLEncoder.encode(file.getName()), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }
        return builder.build();
    }

    /**
     * 请求参数处理
     *
     * @param params
     * @return
     */
    public Map<String, Object> setParams(Object... params) {
        Map<String, Object> mapData = new HashMap<>();
        if (params != null) {
            String key = "";
            for (int i = 1; i <= params.length; i++) {
                if (i % 2 == 0)
                    if (params[i - 1] == null) continue;
                    else mapData.put(key, params[i - 1]);
                else key = (String) params[i - 1];

            }
        }
        return mapData;
    }

}
