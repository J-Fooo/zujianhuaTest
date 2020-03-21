package com.test.createJavaClass;

import com.jf.annotation.bean.RouterBean;
import com.ddd.annotation_api.jrouter_api.JRouterPath;
import com.test.zujianhuatest.Main2Activity;
import com.test.zujianhuatest.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class JRouter$$Path$$App implements JRouterPath {
    @Override
    public Map<String, RouterBean> getPathMap() {
        //app模块注解了多少个activity就添加多少个元素到map中     key：/app/MainActivity   value:RouterBean --- MainActivity.class
        Map<String, RouterBean> pathMap = new HashMap<>();
        pathMap.put("/app/MainActivity",RouterBean.create(RouterBean.TypeEnum.ACTIVITY, MainActivity.class,"/app/MainActivity","app"));
        pathMap.put("/app/Main2Activity",RouterBean.create(RouterBean.TypeEnum.ACTIVITY, Main2Activity.class,"/app/Main2Activity","app"));
        return pathMap;
    }
}
