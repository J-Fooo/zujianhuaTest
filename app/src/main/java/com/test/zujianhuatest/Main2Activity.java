package com.test.zujianhuatest;

import android.os.Bundle;

import com.jf.annotation.JRouter;

import androidx.appcompat.app.AppCompatActivity;
@JRouter(path = "/app/Main2Activity",group = "app")
public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}
