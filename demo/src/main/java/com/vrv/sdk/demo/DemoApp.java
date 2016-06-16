package com.vrv.sdk.demo;

import com.vrv.imsdk.VIMClient;
import com.vrv.sdk.library.SDKApp;
import com.vrv.sdk.library.utils.ToastUtil;

public class DemoApp extends SDKApp {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 SDK  参数为：手机内置sd卡下数据库目录名
        boolean init = VIMClient.init(this, "demo_sdk");
        if (!init) {
            ToastUtil.showShort(this, "SDK 加载失败");
        }
    }
}
