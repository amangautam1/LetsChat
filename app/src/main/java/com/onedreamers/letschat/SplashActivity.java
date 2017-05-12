package com.onedreamers.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_MS = 2000;
    private Handler mHandler;
    private Runnable mRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



            mHandler = new Handler();

            mRunnable = new Runnable() {
                @Override
                public void run() {

                        Intent  intent=new Intent(SplashActivity.this,MainActivity.class);
                        startActivity(intent);
                    finish();

                }
            };

            mHandler.postDelayed(mRunnable, SPLASH_TIME_MS);
        }
    }
