package com.ddd.annotation_api.jrouter_api;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.jf.annotation.bean.RouterBean;

import androidx.annotation.RequiresApi;

public class RouterManager {
    private static final String TAG = "RouterManager";
    private static final String BASE_GROUP = "JRouter$$Group$$";
    private final LruCache<String, JRouterPath> mPathCache;
    private final LruCache<String, JRouterGroup> mGroupCache;
    private String path;
    private String group;

    private RouterManager() {
        mGroupCache = new LruCache<>(50);
        mPathCache = new LruCache<>(50);
    }

    private static RouterManager instance;

    public static RouterManager getInstance() {
        if (instance == null) {
            synchronized (RouterManager.class) {
                if (instance == null) {
                    instance = new RouterManager();
                }
            }
        }
        return instance;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void navigation(Context context, BundleManager bundleManager) {
        String groupName = context.getPackageName() + "." + BASE_GROUP + group;
        Log.d(TAG, "navigation: targetGroupClassName ----- > " + groupName);

        JRouterGroup jRouterGroup = mGroupCache.get(group);
        try {
            if (jRouterGroup == null) {
                jRouterGroup = (JRouterGroup) Class.forName(groupName).newInstance();
                //group缓存起来
                mGroupCache.put(group, jRouterGroup);
            }

            if (jRouterGroup.getGroupMap().isEmpty()) {
                throw new RuntimeException("路由表为空。。。");
            }

            JRouterPath jRouterPath = mPathCache.get(path);
            if (jRouterPath == null) {
//                缓存path
                Class<? extends JRouterPath> jrouterPathClazz = jRouterGroup.getGroupMap().get(group);
                jRouterPath = jrouterPathClazz.newInstance();
                mPathCache.put(path,jRouterPath);
            }

            if (jRouterPath.getPathMap().isEmpty()) {
                throw new RuntimeException("路由表为空。。。");
            }else {
                RouterBean routerBean = jRouterPath.getPathMap().get(path);
                if (routerBean != null) {
                    switch (routerBean.getTypeEnum()) {
                        case ACTIVITY:
                            Class<?> targetClass = routerBean.getMyClass();
                            Intent intent = new Intent(context,targetClass);
                            intent.putExtras(bundleManager.getBundle());
                            context.startActivity(intent);
                            break;
                        default:
                            break;
                    }

                }else {
                    throw new RuntimeException("路由没有注册此activity");
                }
            }


        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "navigation: " + path + " --- " + group);
    }


    /***
     * 路由检查
     * @param path 例如：/order/Order_MainActivity
     *      * @return
     */
    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("router路径错误，正确写法：如 /order/Order_MainActivity");
        }

        if (path.lastIndexOf("/") == 0) { // 只写了一个 /
            throw new IllegalArgumentException("router路径错误，正确写法：如 /order/Order_MainActivity");
        }

        // 截取组名  /order/Order_MainActivity  finalGroup=order
        String finalGroup = path.substring(1, path.indexOf("/", 1)); // finalGroup = order

        if (TextUtils.isEmpty(finalGroup)) {
            throw new IllegalArgumentException("router路径错误，正确写法：如 /order/Order_MainActivity");
        }

        this.path = path;  // 最终的效果：如 /order/Order_MainActivity
        this.group = finalGroup; // 例如：order，personal

        return new BundleManager();
    }


}
