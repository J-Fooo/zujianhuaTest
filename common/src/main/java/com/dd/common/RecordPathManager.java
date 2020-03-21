package com.dd.common;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordPathManager {

    private static final String TAG = "RecordPathManager";
    private static Map<String, List<PathBean>> pathManager = new HashMap<>();

    //添加activity的class进来，根据每个不同模块分开管理
    public static void addPathInfo(String groupName, String targetName, Class clazz) {
        List<PathBean> beanList = pathManager.get(groupName);
        if (beanList == null) {
            beanList = new ArrayList<>();

            beanList.add(new PathBean(targetName, clazz));
        } else {
            for (PathBean pathBean : beanList) {
                if (targetName.equals(pathBean.getPath()))
                    return;
            }
            beanList.add(new PathBean(targetName, clazz));
        }
        pathManager.put(groupName, beanList);
    }

    public static Class getPathInfo(String groupName, String targetName) {
        List<PathBean> beanList = pathManager.get(groupName);
        if (beanList != null) {
            for (PathBean pathBean :
                    beanList) {
                if (targetName.equals(pathBean.getPath())) {
                    return pathBean.getClazz();
                }
            }
        } else {
            Log.d(TAG, "getPathInfo: 未添加的activity信息");
        }
        return null;
    }
}
