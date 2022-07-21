package com.hzzt.powerful.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hzzt.common.dialog.LoadingDialog;
import com.hzzt.common.entity.req.CommonUI;
import com.hzzt.common.entity.resp.CommonResponse;
import com.hzzt.common.interfaces.RvAdapterConvert;
import com.hzzt.common.utils.AppUtil;
import com.hzzt.common.utils.MainUtil;
import com.hzzt.powerful.BR;
import com.hzzt.powerful.R;
import com.twinkle.chat.app.AppViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * @author: Allen
 * @date: 2022/7/21
 * @description: 二次封装Fragment
 */
public abstract class BaseF<V extends ViewDataBinding, VM extends BaseVM> extends BaseFragment<V, VM> {
    private int loadNum = 0;  //记录dialog数

    public Context mContext;
    public Activity mActivity;
    private LoadingDialog loadingDialog;
    //刷新框架
    public SwipeRefreshLayout refreshLayout;
    public Handler handler;
    private List<Disposable> rxBusList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mActivity = getActivity();
    }

    public void removeRxBus() {
        for (Disposable disposable : rxBusList) {
            RxSubscriptions.remove(disposable);
        }
        rxBusList.clear();
    }

    @SuppressLint("CheckResult")
    public void registerBus(Class cls, Consumer consumer) {
        Disposable disposable = RxBus.getDefault().toObservable(cls).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
        RxSubscriptions.add(disposable);
        rxBusList.add(disposable);
    }

    /**
     * rxbus 发送数据
     *
     * @param obj
     */
    public void sendBus(Object obj) {
        RxBus.getDefault().post(obj);
    }

    public void sendStickyBus(Object obj) {
        RxBus.getDefault().postSticky(obj);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        removeRxBus();
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initLayout(inflater, container, savedInstanceState);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        initArgument();
    }

    @Override
    public void initData() {
        super.initData();
        initFlow();
    }

    //装载fragment
    public void addFragment(int frameId, Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(frameId, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    //显示fragment
    public void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    //隐藏fragment
    public void hideFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 接受订阅事件
     */
    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.updateEvent.observe(this, common -> {
            try {
                if (initLayoutUpdate(common) == 0) {
                    if (MainUtil.SERVER_RESPONSE_TAG.equals(common._tag)) {
                        switch (common._code) {
                            case 101:   //网络问题
                                showMsg(R.string.app_error_101);
                                break;
                            case 102:   //api请求失败
                                showMsg(R.string.app_error_102);
                                break;
                            case 104:  //开始请求
                                showLoading((String) common._obj);
                                break;
                            case 105:   //请求结束
                                hideLoading();
                                break;
                            default:   //错误提示
                                if (!TextUtils.isEmpty((String) common._obj))
                                    showMsg((String) common._obj);
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        /**
         * 统一处理服务器response
         */
        viewModel.responseEvent.observe(this, common -> {
            try {
                if (initServerResponse(common) == 0) {
                    if (common.isOk()) {
                        switch (common._type) {
                            case 1:
                                break;
                        }
                    } else if (common.getCode() > 0) {
                        //服务器返回的问题
                    } else {
                        //自身的问题
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 获取当前viewModel的工厂
     *
     * @return
     */
    @Override
    public VM initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(mActivity.getApplication());
        Class<VM> vm = initVM();
        return new ViewModelProvider(this, factory).get(vm == null ? defaultVM() : vm);
    }

    public Class<VM> defaultVM() {
        return (Class<VM>) BaseVM.class;
    }

    public abstract int initLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    public abstract void initArgument();

    public abstract void initFlow();

    public abstract int initLayoutUpdate(CommonUI common);

    public abstract int initServerResponse(CommonResponse common);

    public abstract Class<VM> initVM();

    public void onClickView(View view) {

    }

    @SuppressLint("HandlerLeak")
    public void initHandler() {
        if (handler != null) return;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
            }
        };
    }

    public void handleMsg(Message msg) {
    }


    /**
     * 初始化列表adapter
     *
     * @param layoutId
     * @param data
     * @param rvAdapterConvert
     * @param <T>
     * @return
     */
    public <T> BaseQuickAdapter initQkAdapter(int layoutId, List<T> data, final RvAdapterConvert<T> rvAdapterConvert) {
        BaseQuickAdapter adapter = new BaseQuickAdapter<T, BaseViewHolder>(layoutId, data) {
            @Override
            protected void convert(BaseViewHolder viewHolder, T item) {
                if (rvAdapterConvert != null) {
                    rvAdapterConvert.convert(viewHolder, item);
                }
            }
        };
        return adapter;
    }

    /**
     * 下拉刷新
     *
     * @param refreshLayout
     */
    public void initOnRefresh(SwipeRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
        if (refreshLayout == null) return;
        refreshLayout.setColorSchemeResources(AppUtil.refreshColor);
        refreshLayout.setOnRefreshListener(() -> onRefreshData());
    }

    public void onRefreshData() {
    }

    /**
     * 设置刷新状态
     *
     * @param status
     */
    public void setRefreshStatus(boolean status) {
        if (refreshLayout == null || status == refreshLayout.isRefreshing()) return;
        refreshLayout.setRefreshing(status);
    }

    /**
     * 设置刷新是否可用
     *
     * @param status
     */
    public void setRefreshEnabled(boolean status) {
        if (refreshLayout == null) return;
        refreshLayout.setEnabled(status);
    }

    //intent page
    public void startActivity(Class<?> clz) {
        if (isAdded()) {
            if (mContext == null) mContext = getActivity();
            if (mContext != null && AppUtil.isMultiClickClick(1000)) {
                startActivity(new Intent(mContext, clz));
            }
        }
    }

    public void showLoading(String loadingText) {
        if (loadingDialog != null) {
            if (!loadingDialog.isShowing()) {
                loadNum = 1;
                loadingDialog.show();
            } else {
                ++loadNum;
            }
        } else {
            loadNum = 1;
            loadingDialog = LoadingDialog.getInstance(mContext, loadingText);
            loadingDialog.showDialog();
        }
    }

    public void hideLoading() {
        --loadNum;
        if (loadNum == 0) {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    //关闭所有
    public void closeLoading() {
        loadNum = 1;
        hideLoading();
    }

    /**
     * 吐司
     *
     * @param msg
     */
    public void showMsg(String msg) {
        ToastUtils.showShort(msg);
    }

    public void showMsg(int resMsg) {
        ToastUtils.showShort(getResToStr(resMsg));
    }

    public Drawable getResDraw(int drawableId) {
        return ContextCompat.getDrawable(getContext(), drawableId);
    }

    public int getResColor(int colorId) {
        return ContextCompat.getColor(getContext(), colorId);
    }

    public int getResColor(String color) {
        return Color.parseColor(color);
    }

    //获取string
    public String getResToStr(int res, int value) {
        return String.format(getResources().getString(res), value);
    }

    //获取string
    public String getResToStr(int res) {
        return getResources().getString(res);
    }

    public String getResToStr(int res, String value) {
        return String.format(getResources().getString(res), value);
    }

    /**
     * 判断是否登录，否则跳转登录页
     *
     * @return
     */
    public boolean isLogin() {
        if (getActivity() instanceof BaseA) {
            return ((BaseA) getActivity()).isLogin();
        }
        return true;
    }


}
