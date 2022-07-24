package com.hzzt.common.widget.web;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.FragmentActivity;


import com.hzzt.common.R;
import com.hzzt.common.dialog.LoadingDialog;
import com.hzzt.common.utils.AppUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.ToastUtils;


/**
 * @author: Allen
 * @date: 2021/6/21
 * @description:
 */
public class ComWebView extends WebView {
    public static String WEB_URL = "myWebUrl";
    public static String WEB_TITLE = "myWebTitle";
    public static final int REQUEST_FILE_PICKER = 1;
    public static ValueCallback<Uri> mFilePathCallback;
    public static ValueCallback<Uri[]> mFilePathCallbacks;
    private FragmentActivity mContext;
    private LoadingDialog dialog;
    private TitleCallBack callBack;

    public ComWebView(Context context) {
        super(context);
        initUI(context);
        this.mContext = (FragmentActivity) context;
    }

    public ComWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
        this.mContext = (FragmentActivity) context;
    }

    public ComWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context);
        this.mContext = (FragmentActivity) context;
    }

    public void setmContext(Activity mContext) {
        this.mContext = (FragmentActivity) mContext;
    }

    private void initUI(Context context) {
//        if (getX5WebViewExtension() != null) {
//            getX5WebViewExtension().setScrollBarFadingEnabled(false);
//        }
        setHorizontalScrollBarEnabled(false);//水平不显示小方块
        setVerticalScrollBarEnabled(false); //垂直不显示小方块

//      setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//滚动条在WebView内侧显示
//      setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条在WebView外侧显示
        initWebViewSettings();
    }

    //   基本的WebViewSetting
    @JavascriptInterface
    public void initWebViewSettings() {
        setBackgroundResource(R.drawable.bg_rect_white_top_12dp);
        setWebViewClient(client);
        setWebChromeClient(chromeClient);
        setDownloadListener(downloadListener);
        setClickable(true);
        setOnTouchListener((v, event) -> false);
        WebSettings webSetting = getSettings();
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL); //适应内容大小
        webSetting.setDefaultTextEncodingName("UTF-8");
        webSetting.setJavaScriptEnabled(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setSupportZoom(false);
        webSetting.setTextSize(WebSettings.TextSize.NORMAL);  //NORMAL
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAllowFileAccessFromFileURLs(true);
        webSetting.setAllowUniversalAccessFromFileURLs(true);
        webSetting.setAllowContentAccess(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //android 默认是可以打开_bank的，是因为它默认设置了WebSettings.setSupportMultipleWindows(false)
        //在false状态下，_bank也会在当前页面打开……
        //而x5浏览器，默认开启了WebSettings.setSupportMultipleWindows(true)，
        // 所以打不开……主动设置成false就可以打开了
        //需要支持多窗体还需要重写WebChromeClient.onCreateWindow
        webSetting.setSupportMultipleWindows(false);
//        webSetting.setCacheMode(WebSettings.LOAD_NORMAL);
//        getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension

        //扩大比例的缩放
        webSetting.setUseWideViewPort(true);
        //自适应屏幕
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setLoadWithOverviewMode(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.canGoBack()) {
            this.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private WebChromeClient chromeClient = new WebChromeClient() {
        //监听进度
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                try {
                    if (dialog != null &&!mContext.isFinishing()) {
                        dialog.dismissDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * js上传文件的<input type="file" name="fileField" id="fileField" />事件捕获
         * Android > 4.1.1 调用这个方法
         */
        @Deprecated
        public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
            mFilePathCallback = filePathCallback;
          //  AppUtil.openAlbum(mContext);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            mFilePathCallbacks = filePathCallback;
          //  AppUtil.openAlbum(mContext);
            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            KLog.d("-----title----"+title);
            if(callBack!=null) callBack.setTitle(title);
        }
    };


    private WebViewClient client = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (dialog == null) dialog = LoadingDialog.getInstance(mContext, "");
            if (!dialog.isShowing()&&!mContext.isFinishing()) {
                dialog.showDialog();
            }
        }

        //当页面加载完成的时候
        @Override
        public void onPageFinished(WebView webView, String url) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            String endCookie = cookieManager.getCookie(url);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.getInstance().sync();//同步cookie
            } else {
                CookieManager.getInstance().flush();
            }
            super.onPageFinished(webView, url);
            try {
                //传值h5
//                AppUtil.sendTokenToH5(webView);

                if (dialog != null &&!mContext.isFinishing()) {
                    dialog.dismissDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //返回值是true的时候控制去WebView打开，
            // 为false调用系统浏览器或第三方浏览器
            if (url.startsWith("http") || url.startsWith("https")) {
                return false;
            } else {
                try {
                    /**
                     * mobikwik://
                     *  PhonePe://
                     *  paytm://
                     *  paytmmp://
                     *  gpay://
                     *  upi://
                     */
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));

                    if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                        view.getContext().startActivity(intent);
                    } else {
                        goBack();
                        ToastUtils.showShort("Please download and install first");
                    }
                } catch (ActivityNotFoundException e) {
                }
                return true;
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            try {
                if (dialog != null &&!mContext.isFinishing()) {
                    dialog.dismissDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void syncCookie(String url, String cookie) {
        CookieSyncManager.createInstance(getContext());
        if (!TextUtils.isEmpty(url)) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();// 移除
            cookieManager.removeAllCookie();

            //这里的拼接方式是伪代码
            String[] split = cookie.split(";");
            for (String string : split) {
                //为url设置cookie
                // ajax方式下  cookie后面的分号会丢失
                cookieManager.setCookie(url, string);
            }
            String newCookie = cookieManager.getCookie(url);
            //sdk21之后CookieSyncManager被抛弃了，换成了CookieManager来进行管理。
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.getInstance().sync();//同步cookie
            } else {
                CookieManager.getInstance().flush();
            }
        } else {
        }
    }

    //删除Cookie
    private void removeCookie() {
        CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
        }
    }

    public String getDoMain(String url) {
        String domain = "";
        int start = url.indexOf(".");
        if (start >= 0) {
            int end = url.indexOf("/", start);
            if (end < 0) {
                domain = url.substring(start);
            } else {
                domain = url.substring(start, end);
            }
        }
        return domain;
    }

    /**
     * 下载监听
     */
    DownloadListener downloadListener = (url, userAgent, contentDisposition, mimetype, contentLength) -> {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getContext().startActivity(intent);
    };

    /**
     * 注册js和android通信桥梁对象
     *
     * @param obj 桥梁类对象,该对象提供方法让js调用,默认开启JavaScriptEnabled=true
     */
    public void addBridgeInterface(Object obj, String name) {
        this.getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new MyJavaScriptMethod(obj), name);
    }

    /**
     * 内置js桥梁类
     */
    public class MyJavaScriptMethod {
        private Object mTarget;
        private Method targetMethod;

        public MyJavaScriptMethod(Object targer) {
            this.mTarget = targer;
        }

        /**
         * 内置桥梁方法
         *
         * @param method 方法名
         * @param json   js传递参数，json格式
         */
        @JavascriptInterface
        public void invokeMethod(String method, String[] json) {
            Class<?>[] params = new Class[]{String[].class};
            try {
                Method targetMethod = this.mTarget.getClass().getDeclaredMethod(method, params);
                targetMethod.invoke(mTarget, new Object[]{json});//反射调用js传递过来的方法，传参
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    public void setCallBack(TitleCallBack callBack) {
        this.callBack = callBack;
    }

    public interface TitleCallBack{
        void setTitle(String title);
    }
}
