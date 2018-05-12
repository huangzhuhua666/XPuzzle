package com.hzh.xpuzzle.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * 获取屏幕相关参数
 */
public class ScreenUtils {

    /**
     * 获取屏幕相关参数
     *
     * @param context 上下文环境
     * @return DisplayMetrics 屏幕宽高
     */
    public static DisplayMetrics getScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = manager.getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
    }

    /**
     * dp2px
     *
     * @param context 上下文环境
     * @param dpValue dp
     * @return px
     */
    public static int dp2px(Context context, int dpValue) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return (int) (dpValue * metrics.density);
    }
}
