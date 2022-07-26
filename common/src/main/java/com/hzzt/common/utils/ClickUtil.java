package com.hzzt.common.utils;

/**
 * @author: Allen
 * @date: 2022/7/25
 * @description:
 */
public class ClickUtil {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    /**
     * 已连接的防点
     */
    public static long connectLastClickTime;
    public static boolean isConnectClick(int minTime) {
        boolean flag = false;
        if (minTime == 0)
            minTime = 1000;
        try {
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - connectLastClickTime) >= minTime || (curClickTime - connectLastClickTime) < 0) {
                flag = true;
                connectLastClickTime = curClickTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 已连接的防点
     */
    public static long disconnectLastClickTime;
    public static boolean isDisconnectClick(int minTime) {
        boolean flag = false;
        if (minTime == 0)
            minTime = 1000;
        try {
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - disconnectLastClickTime) >= minTime || (curClickTime - disconnectLastClickTime) < 0) {
                flag = true;
                disconnectLastClickTime = curClickTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
