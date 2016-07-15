package com.vrv.sdk.demo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.vrv.imsdk.VIMClient;
import com.vrv.sdk.library.utils.ToastUtil;

public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //debug
        VIMClient.setDebugMode(true);
        // 初始化 SDK  参数为：手机内置sd卡下数据库目录名
        boolean init = VIMClient.init(this, "demo_sdk");
        if (!init) {
            ToastUtil.showShort(this, "SDK 加载失败");
        }

        //百度地图初始化
        SDKInitializer.initialize(this);
    }
}
