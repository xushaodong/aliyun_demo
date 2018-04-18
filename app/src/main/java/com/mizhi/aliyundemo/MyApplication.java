package com.mizhi.aliyundemo;

import android.app.Application;

import com.aliyun.common.httpfinal.QupaiHttpFinal;

/**
 * 类描述：
 *
 * @Author 许少东
 * Created at 2018/4/17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        loadLibs();
        QupaiHttpFinal.getInstance().initOkHttpFinal();
        com.aliyun.vod.common.httpfinal.QupaiHttpFinal.getInstance().initOkHttpFinal();
    }

    private void loadLibs() {
        System.loadLibrary("live-openh264");
        System.loadLibrary("QuCore-ThirdParty");
        System.loadLibrary("QuCore");
    }
}
