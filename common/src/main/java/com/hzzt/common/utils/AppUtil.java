package com.hzzt.common.utils;

import android.content.Context;

import com.hzzt.common.R;

/**
 * @author: Allen
 * @date: 2022/7/22
 * @description:
 */
public class AppUtil {
    //刷新颜色配置
    public static final int[] refreshColor = {R.color.red, R.color.yellow, R.color.green};
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
}
