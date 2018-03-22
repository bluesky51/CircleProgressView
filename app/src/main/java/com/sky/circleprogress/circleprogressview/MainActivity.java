package com.sky.circleprogress.circleprogressview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircleProgressView circleProgressView = findViewById(R.id.circleProgressView);
        circleProgressView.setStartAngle(90);
        circleProgressView.setCurrProgress(70);

    }


}
