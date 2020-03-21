package com.ddd.annotation_api.jrouter_api;


import com.jf.annotation.bean.RouterBean;

import java.util.Map;

/**
 *  其实就是 路由组 Group 对应的 ---- 详细Path加载数据接口 JRouterPath
 *  例如：order分组 对应 ---- 有那些类需要加载（Order_MainActivity  Order_MainActivity2 ...）
 */
public interface JRouterPath {

    /**
     * 例如：order分组下有这些信息，personal分组下有这些信息
     *
     * @return key:"/order/Order_MainActivity"   或  "/personal/Personal_MainActivity"
     *         value: RouterBean==Order_MainActivity.class 或 RouterBean=Personal_MainActivity.class
     */
    Map<String, RouterBean> getPathMap();

}
