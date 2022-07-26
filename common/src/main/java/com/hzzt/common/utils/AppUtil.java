package com.hzzt.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzzt.common.R;
import com.hzzt.common.data.cache.CacheData;
import com.hzzt.common.entity.resp.CurrentServerResp;
import com.hzzt.common.entity.resp.ServerResp;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;

import lombok.val;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * @author: Allen
 * @date: 2022/7/22
 * @description:
 */
public class AppUtil {
    //刷新颜色配置
    public static final int[] refreshColor = {R.color.red, R.color.yellow, R.color.green};

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 利用反射获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 1s 内不可重复点击(单位 毫秒)
     *
     * @return
     */
    public static long lastClickTime;
    public static boolean isMultiClickClick(int minTime) {
        boolean flag = false;
        if (minTime == 0)
            minTime = 1000;
        try {
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= minTime || (curClickTime - lastClickTime) < 0) {
                flag = true;
                lastClickTime = curClickTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 退出
     *
     * @return
     */
    private static long mExitTime;
    public static boolean doubleClickExit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.showShort(R.string.app_exit);
            mExitTime = System.currentTimeMillis();
            return false;
        }
        return true;
    }


    @SuppressLint("CheckResult")
    public static void openAlbum(FragmentActivity activity){
        RxPermissions permissions = new RxPermissions(activity);
        permissions.request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        ).subscribe(aBoolean -> {
            if(aBoolean){
//                PictureSelector.create(activity)
//                        .openGallery(PictureMimeType.ofImage())
//                        .maxSelectNum(1)
//                        .imageEngine(GlideEngine.createGlideEngine())
//                        .compressQuality(90) // 图片压缩后输出质量
//                        .minimumCompressSize(1024) // 小于1M的图片不压缩
//                        .isEnableCrop(false)
//                        .forResult(PictureConfig.CHOOSE_REQUEST);
            }
        });
    }

    /**
     * 读取assets 数据
     * @param context
     * @param fileName
     * @return
     */
    public static String getAssetsJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 随机一个
     * @param num
     * @return
     */
    public static int getRandNum(int num){
        return new Random().nextInt(num);
    }


    /**
     * 随机取一个服务器
     * @param context
     * @return
     */
    public static CurrentServerResp getRandomServer(Context context){
        String serverJson=getAssetsJson(context, "server_config.json");
        Type listType = new TypeToken<List<ServerResp>>() {}.getType();
        List<ServerResp> list=new Gson().fromJson(serverJson, listType);
        if(list.size()>0){
            ServerResp serverResp=list.get(getRandNum(list.size()));

            ServerResp.ServerDTO serverChild=serverResp.getServer().get(getRandNum(serverResp.getServer().size()));

            CurrentServerResp currentConfig=new CurrentServerResp(serverResp.getCountry(),serverResp.getIconUrl(),
                    serverChild.getSerUrl(), serverChild.getKey(), serverChild.getWeight());
            CacheData.INSTANCE.setServerConfig(currentConfig);  //存储当前配置
            return currentConfig;
        }

        return null;
    }

    //获取正在运行的服务
    public static void getRunningService(Activity activity){
        ActivityManager activityManger = (ActivityManager) activity.getSystemService(activity.ACTIVITY_SERVICE);// 获取Activity管理器
        List<ActivityManager.RunningServiceInfo> serviceList = activityManger.getRunningServices(30);// 从窗口管理器中获取正在运行的Service
        for(ActivityManager.RunningServiceInfo info:serviceList){
            KLog.d("----->>"+info.service.getClassName()+"\n");
        }
    }
}
