package com.test.zujianhuatest;

import android.app.Application;

import com.dd.common.RecordPathManager;
import com.dd.order.Order_MainActivity;
import com.dd.personal.Personal_MainActivity;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RecordPathManager.addPathInfo("main","Main_Activity",MainActivity.class);
        RecordPathManager.addPathInfo("order","Order_Main_Activity", Order_MainActivity.class);
        RecordPathManager.addPathInfo("personal","Personal_Main_Activity", Personal_MainActivity.class);
    }
}
