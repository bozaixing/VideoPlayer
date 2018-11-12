package com.bozaixing.media.util;

import android.os.Bundle;
import android.view.View;

import java.util.Formatter;
import java.util.Locale;

/**
 * Author:  bozaixing
 * Email:   654152983@qq.com
 * Date:    2018/11/9
 * Descr:   视频处理的相关工具类
 */
public class VideoUtil {

    /**
     * 私有化的构造方法，禁止通过new的方式创建对象
     */
    private VideoUtil(){

        // 抛出不支持的操作异常
        throw new UnsupportedOperationException("cannot be instantiated!");
    }


    /**
     * 将毫秒数转换为“##：##”格式的时间
     *
     * @param milliseconds      毫秒数
     * @return
     */
    public static String formatTime(int milliseconds){
        // 校验数据
        if (milliseconds <= 0 || milliseconds >= 1000 * 60 * 60 * 24){
            // 如果毫秒数小于0或者大于24小时
            return "00:00";
        }
        // 将毫秒转换为秒
        int totalSeconds = milliseconds / 1000;
        // 需要显示的秒数
        long seconds = totalSeconds % 60;
        // 需要显示的分钟数
        long minutes = (totalSeconds / 60) % 60;
        // 需要显示的小时数
        long hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }



    /**
     * 获取View的屏幕属性
     *
     * @param view
     * @return
     */
    public static final String VIEW_SCREEN_LOCATION_LEFT = "view_screen_location_left";
    public static final String VIEW_SCREEN_LOCATION_TOP = "view_screen_location_top";
    public static final String VIEW_WIDTH = "view_width";
    public static final String VIEW_HEIGHT = "view_height";
    public static Bundle getViewProperty(View view){
        Bundle bundle = new Bundle();
        int[] screenLocation = new int[2];
        // 获取View在整个屏幕中的位置
        view.getLocationOnScreen(screenLocation);
        bundle.putInt(VIEW_SCREEN_LOCATION_LEFT, screenLocation[0]);
        bundle.putInt(VIEW_SCREEN_LOCATION_TOP, screenLocation[1]);
        bundle.putInt(VIEW_WIDTH, view.getWidth());
        bundle.putInt(VIEW_HEIGHT, view.getHeight());
        return bundle;
    }




}
