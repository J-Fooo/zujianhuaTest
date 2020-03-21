package com.dd.personal;

import android.os.Bundle;

import com.jf.annotation.JRouter;

import androidx.appcompat.app.AppCompatActivity;
@JRouter(path = "/personal/Personal_Main2Activity")
public class PersonalMain2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity_main2);
    }
}
