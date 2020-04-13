package com.flux.quickloans;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ActivitySplash extends XActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isInducted = data.getBoolean(XClass.inducted, false);
                if (isInducted) startActivity(new Intent(getApplicationContext(), ActivityMain.class));
                else startActivity(new Intent(getApplicationContext(), ActivityIntro.class));
                finish();
            }
        }, 2200);
    }
}