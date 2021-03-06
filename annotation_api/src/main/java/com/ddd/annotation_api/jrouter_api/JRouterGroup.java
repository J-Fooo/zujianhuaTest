package com.ddd.annotation_api.jrouter_api;

import java.util.Map;

public interface JRouterGroup {

    /**
     * 例如：order分组下有这些信息，personal分组下有这些信息
     * 例如："order" --- ARouterPath的实现类 -->（APT生成出来的 ARouter$$Path$$order）
     *
     * @return  key:"order"   value:系列的order组下面所有的（path---class）
     */
    Map<String, Class<? extends JRouterPath>> getGroupMap();

}
