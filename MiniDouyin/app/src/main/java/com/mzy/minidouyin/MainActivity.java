package com.mzy.minidouyin;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Timer timer=new Timer();

        TimerTask task=new TimerTask()
        {
            @Override
            public void run(){
                startActivity(new Intent(MainActivity.this, VideoList.class));
                finish();
            }
        };
        timer.schedule(task,3000);
    }
}
