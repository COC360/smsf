package com.com.utils;

import android.content.Context;

import java.lang.Thread.UncaughtExceptionHandler;


/**
 * 抓取全局未捕获的异常, 最后的crash 处理点
 * Created by xixixxx on 2017/11/12.
 */

public class CrashHandler implements UncaughtExceptionHandler {
    // 程序的 Context 对象
    private Context mContext;

    // 系统默认的 UncaughtException 处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // CrashHandler 实例
    private static CrashHandler INSTANCE = new CrashHandler();

    /** 保证只有一个 CrashHandler 实例 */
    private CrashHandler() {
    }

    /** 获取 CrashHandler 实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context){
        mContext = context;
        // 获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该 CrashHandler 为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("crash handler");
    }
}
