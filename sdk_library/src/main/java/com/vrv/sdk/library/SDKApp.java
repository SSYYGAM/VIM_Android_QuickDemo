package com.vrv.sdk.library;

import android.app.Application;
import android.content.Context;

public class SDKApp extends Application {

    public static final String ACCOUNT = "008611012347002";
    public static final String PASSWORD = "qaz12345";
    public static final String SERVER = "im";
    public static final String NATIONAL_CODE = "0086";

    protected static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
    }

    public static Context getContext() {
        return context;
    }

}
