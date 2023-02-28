package com.example.otp_authentication.Util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.provider.Settings;

import com.example.otp_authentication.R;

public class AppUtils {
    Context context;

    private AppUtils(Context context) {
        this.context = context.getApplicationContext();
    }

    private static AppUtils instance;
    public static AppUtils getInstance(Context context) {
        if (instance == null) {
            instance = new AppUtils(context);
        }
        return instance;
    }

    @SuppressLint("HardwareIds")
    public String getDeviceID(){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

}
