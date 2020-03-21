package com.test.createJavaClass;

import com.ddd.annotation_api.jrouter_api.JRouterGroup;
import com.ddd.annotation_api.jrouter_api.JRouterPath;

import java.util.HashMap;
import java.util.Map;

public class JRouter$$Group$$App implements JRouterGroup {
    @Override
    public Map<String, Class<? extends JRouterPath>> getGroupMap() {
        //将app模块创建的JRouter.class添加到map     key:app   value:JRouter$$Path$$App.class
        Map<String, Class<? extends JRouterPath>> groupMap = new HashMap<>();
        groupMap.put("app",JRouter$$Path$$App.class);
        return groupMap;
    }
}
