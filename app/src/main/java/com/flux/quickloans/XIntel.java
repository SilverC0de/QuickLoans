package com.flux.quickloans;

import android.app.Application;

import co.paystack.android.PaystackSdk;

public class XIntel extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PaystackSdk.initialize(getApplicationContext());
    }
}
