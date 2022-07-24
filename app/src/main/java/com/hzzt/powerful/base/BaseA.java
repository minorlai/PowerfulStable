package com.hzzt.powerful.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gyf.immersionbar.ImmersionBar;
import com.hzzt.common.data.cache.CacheData;
import com.hzzt.common.data.http.RetrofitHelper;
import com.hzzt.common.dialog.LoadingDialog;
import com.hzzt.common.entity.req.CommonUI;
import com.hzzt.common.entity.resp.CommonResponse;
import com.hzzt.common.interfaces.RvAdapterConvert;
import com.hzzt.common.utils.MainUtil;
import com.hzzt.powerful.BR;
import com.hzzt.powerful.R;
import com.hzzt.powerful.app.App;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.twinkle.chat.app.AppViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxBusSubscriber;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ConvertUtils;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * @author: Allen
 * @date: 2022/7/21
 * @description: base 二次封装
 */
public abstract class BaseA<V extends ViewDataBinding, VM extends BaseVM> extends BaseActivity<V, VM> implements LifecycleOwner {
    private String TAG = "BaseA";
    protected LoadingDialog loadingDialog;
//    private SwipeBackActivityHelper mHelper;
    private volatile int loadNum = 0;
    public Intent aIntent;
    public SwipeRefreshLayout refreshLayout;
    public Handler handler;
    public Activity activity;
    private List<Disposable> rxBusList = new ArrayList<>();
    public boolean isTouchCollBd = true;
    public boolean isDialogTran = true;

    @SuppressLint("CheckResult")
    public void registerBus(Class cls, Consumer consumer) {
        Disposable disposable = RxBus.getDefault().toObservable(cls).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
        RxSubscriptions.add(disposable);
        rxBusList.add(disposable);
    }


    public void removeRxBus() {
        for (Disposable disposable : rxBusList) {
            RxSubscriptions.remove(disposable);
        }
        rxBusList.clear();
    }

    @Override
    protected void onDestroy() {
        viewModel.updateEvent.removeObservers(this);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        closeLoading();
        removeRxBus();
        super.onDestroy();
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        if (isDialogTran)
//            mHelper.onPostCreate();
//    }

    @Override
    public <T extends View> T findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null) {
            return super.findViewById(id);
        }
        return (T) v;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

//    @Override
//    public SwipeBackLayout getSwipeBackLayout() {
//        return mHelper.getSwipeBackLayout();
//    }
//
//    @Override
//    public void setSwipeBackEnable(boolean enable) {
//        if (isDialogTran)
//            getSwipeBackLayout().setEnableGesture(enable);
//    }
//
//    @Override
//    public void scrollToFinishActivity() {
//        Utils.convertActivityToTranslucent(this);
//        getSwipeBackLayout().scrollToFinishActivity();
//    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return initLayout(savedInstanceState);
    }


    @Override
    public void initParam() {
        super.initParam();
        activity = this;
        BaseVM.initContext(activity);
        aIntent = getIntent();
        if (aIntent != null) {
            initArgument();
        }
    }

    @Override
    public void initData() {
        super.initData();
//        mHelper = new SwipeBackActivityHelper(this);  //页面侧滑返回
//        if (isDialogTran) {
//            mHelper.onActivityCreate();
//        }
        initImmersionBar();   //沉浸式
        initFlow();  //view 操作
        setLocale(new Locale("en"));
    }

    //装载fragment
    public void addFragment(int frameId, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(frameId, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void addFragment(int frameId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(frameId, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 初始化状态栏
     */
    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .transparentBar()                //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
                // .keyboardEnable(true)          //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
//                .statusBarDarkFont(true)     //状态栏字体是深色，不写默认为亮色
                .init();
        if (ImmersionBar.hasNavigationBar(this)) { //判断是否有导航栏，有则单独设置导航栏颜色与透明度
            ImmersionBar.with(this).fullScreen(false).navigationBarColor(R.color.black).init();
        }
    }

    /**
     * 订阅结果
     */
    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.updateEvent.observe(this, common -> {
            try {
                int code = initLayoutUpdate(common);
                if (code >= 0) {
                    if (MainUtil.SERVER_RESPONSE_TAG.equals(common._tag)) {
                        switch (common._code) {
                            case 101:   //网络问题
                                showMsg(R.string.app_error_101);
                                break;
                            case 102:   //api请求失败
                                showMsg(R.string.app_error_102);
                                break;
                            case 104:  //开始请求
                                showLoading("");
                                break;
                            case 105:   //请求结束
                                hideLoading(false);
                                break;
                            default:    //错误提示
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
         * 服务器响应的统一处理入口
         */
        viewModel.responseEvent.observe(this, result -> {
            try {
                int code = initServerResponse(result);
                if (code >= 0) {
                    if (result.isOk()) {  //请求success
                        switch (result._type) {  //请求类型
                            case 1:
                                break;
                        }
                    } else if (result.getCode() > 1) {
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

    @Override
    public VM initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        Class<VM> vm = initVM();
        return new ViewModelProvider(this, factory).get(vm == null ? defaultVM() : vm);
    }


    public Class<VM> defaultVM() {
        return (Class<VM>) BaseVM.class;
    }

    public abstract int initLayout(Bundle savedInstanceState);

    public abstract void initArgument();

    public abstract void initFlow();

    public void onClickView(View view) {
    }

    public void onFragmentClickView(View view) {

    }

    public abstract int initLayoutUpdate(CommonUI common);

    public abstract int initServerResponse(CommonResponse common);

    public abstract Class<VM> initVM();


    @SuppressLint("HandlerLeak")
    public void initHandler() {
        if (handler != null) return;
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (!isDestroyed() || !isFinishing()) {
                    handleMsg(msg);
                } else {
                    KLog.d(TAG, "页面已销毁，不再处理handler消息");
                }
            }
        };
    }

    public void handleMsg(Message msg) {
    }


    /**
     * 适配器数据
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
     * 刷新
     *
     * @param refresh
     */
    public void initOnRefresh(SwipeRefreshLayout refresh) {
        this.refreshLayout = refresh;
        refreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.blue);
        refreshLayout.setOnRefreshListener(() -> onRefreshData());
    }

    //刷新方法
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

    /**
     * 显示对话框
     *
     * @param loadTxt
     */
    public void showLoading(String loadTxt) {
        if (loadingDialog != null) {
            if (!loadingDialog.isShowing()) {
                loadNum = 1;
                if (!isDestroyed()) {
                    loadingDialog.show();
                }
            } else {
                ++loadNum;
            }
        } else {
            loadNum = 1;
            if (!isDestroyed()) {
                loadingDialog = LoadingDialog.getInstance(activity, loadTxt);
                loadingDialog.show();
            }
        }
    }

    /**
     * 显示对话框
     */
    public void showLoading() {
        if (loadingDialog != null) {
            if (!loadingDialog.isShowing()) {
                loadNum = 1;
                if (!isDestroyed()) {
                    loadingDialog.show();
                }
            } else {
                ++loadNum;
            }
        } else {
            loadNum = 1;
            if (!isDestroyed()) {
                loadingDialog = LoadingDialog.getInstance(activity, "");
                loadingDialog.show();
            }
        }
    }

    /**
     * 隐藏对话框
     *
     * @param doDestroy
     */
    public void hideLoading(boolean doDestroy) {
        --loadNum;
        if (loadNum == 0) {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
        if (doDestroy) {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }
    }

    /**
     * 关闭对话框
     */
    public void closeLoading() {
        loadNum = 1;
        hideLoading(true);
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

    //设置颜色
    public int getResColor(int colorId) {
        return ContextCompat.getColor(this, colorId);
    }

    public int getResColor(String color) {
        return Color.parseColor(color);
    }

    //获取string
    public String getResToStr(int res, int value) {
        return String.format(getResources().getString(res), value);
    }

    //获取string
    public String getResToStr(int res, int value, int value1) {
        return String.format(getResources().getString(res), value, value1);
    }

    //获取string
    public String getResToStr(int res) {
        return getResources().getString(res);
    }

    //format 形式
    public String getResToStr(int res, String value) {
        return String.format(getResources().getString(res), value);
    }

    //format 形式
    public String getResToStr(String resTxt, String value) {
        return String.format(resTxt, value);
    }

    /**
     * 判断是否登录，否则跳转登录页
     *
     * @return
     */
    public boolean isLogin() {
        if (!TextUtils.isEmpty(CacheData.INSTANCE.getUserToken())) {
            return true;
        }
        return false;
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 跟随系统字体
     *
     * @return
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.fontScale = 1;
//            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    /*  *//**
     * 点击空白区域隐藏键盘.
     *//*
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN && isTouchCollBd) {  //把操作放在用户点击的时候
            View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (KeyboardUtil.isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                KeyboardUtil.hideKeyboard(v.getWindowToken(), activity);   //收起键盘
            }
        }
        return super.dispatchTouchEvent(me);
    }*/


    public static void setLocale(Locale locale) {
        Context context = App.app;
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale.setDefault(locale);
        configuration.setLocale(locale);
            context = context.getApplicationContext().createConfigurationContext(configuration);
            context = context.createConfigurationContext(configuration);
        context.getResources().updateConfiguration(configuration,
                resources.getDisplayMetrics());
    }

}
