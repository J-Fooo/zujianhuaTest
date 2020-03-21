package com.dd.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ddd.annotation_api.jrouter_api.JRouterGroup;
import com.ddd.annotation_api.jrouter_api.JRouterPath;
import com.ddd.annotation_api.jrouter_api.RouterManager;
import com.jf.annotation.JRouter;

import androidx.appcompat.app.AppCompatActivity;
@JRouter(path = "/order/Order_MainActivity")
public class Order_MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main);
    }

    public void toMain(View view) {
        //组件工程没依赖任何其他组件，通过类加载获取目标activity跳转
        /*try {
            Class<?> personalActivity = Class.forName("com.dd.personal.Personal_MainActivity");
            Intent intent = new Intent(this,personalActivity);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
        String baseGroup = "JRouter$$Group$$";
        String mGroup = "personal";
        try {

            String mClassName = this.getPackageName() +"."+ baseGroup + mGroup;
            Class clazz = Class.forName(mClassName);
            JRouterGroup jRouterGroup = (JRouterGroup) clazz.newInstance();
            Class<? extends JRouterPath> pathMapClazz = jRouterGroup.getGroupMap().get(mGroup);
            JRouterPath jRouterPath = pathMapClazz.newInstance();
            Class<?> targetClass = jRouterPath.getPathMap().get("/personal/Personal_MainActivity").getMyClass();

            Intent intent = new Intent(this,targetClass);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toPersonal(View view) {
        //通过全局map维护的路由信息获取目标activity跳转
        /*Class personalActivityClass = RecordPathManager.getPathInfo("personal", "Personal_Main_Activity");
        Intent intent = new Intent(this,personalActivityClass);
        startActivity(intent);*/

        RouterManager.getInstance().build("/personal/Personal_MainActivity")
                .putString("ddd","test")
                .putInt("num",188)
                .navigation(this);
    }
}
