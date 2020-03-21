package com.dd.personal;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jf.annotation.JRouter;

import androidx.appcompat.app.AppCompatActivity;
@JRouter(path = "/personal/Personal_MainActivity")
public class Personal_MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity_main);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String str = bundle.getString("ddd");
            int num = (int) bundle.get("num");
            Toast.makeText(this,str + " --------- " + num,Toast.LENGTH_SHORT).show();
        }
    }

    public void toast(View view) {
        Toast.makeText(this,"Personal",Toast.LENGTH_SHORT).show();
    }
}
