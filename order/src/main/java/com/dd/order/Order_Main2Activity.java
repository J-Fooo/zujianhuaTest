package com.dd.order;

import android.os.Bundle;

import com.jf.annotation.JRouter;

import androidx.appcompat.app.AppCompatActivity;
@JRouter(path = "/order/Order_Main2Activity")
public class Order_Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main2);
    }
}
