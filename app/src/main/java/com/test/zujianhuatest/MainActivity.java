package com.test.zujianhuatest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dd.order.Order_MainActivity;
import com.dd.personal.Personal_MainActivity;
import com.jf.annotation.JRouter;
import com.jf.annotation.Parameter;

import androidx.appcompat.app.AppCompatActivity;

@JRouter(path = "/app/MainActivity",group = "app")
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Parameter
    String name;

    @Parameter
    String age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toOrder(View view) {
        //主工程依赖了组件工程，activity直接跳转组件activity
        Intent intent = new Intent(this, Order_MainActivity.class);
        startActivity(intent);
    }

    public void toPersonal(View view) {
        Intent intent = new Intent(this, Personal_MainActivity.class);
        startActivity(intent);
    }

}
