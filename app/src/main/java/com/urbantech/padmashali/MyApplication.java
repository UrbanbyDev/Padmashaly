package com.urbantech.padmashali;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.facebook.FacebookSdk;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.urbantech.utils.Constant;
import com.urbantech.utils.DBHelper;
import com.urbantech.utils.SharedPref;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPref sharedPref = new SharedPref(this);
        String mode = sharedPref.getDarkMode();
        switch (mode) {
            case Constant.DARK_MODE_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case Constant.DARK_MODE_OFF:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case Constant.DARK_MODE_ON:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }

        FirebaseAnalytics.getInstance(getApplicationContext());

        OneSignal.startInit(getApplicationContext()).init();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/pop_reg.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        try {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            dbHelper.onCreate(dbHelper.getWritableDatabase());
        } catch (Exception e) {
            e.printStackTrace();
        }

        MobileAds.initialize(getApplicationContext());

        FacebookSdk.sdkInitialize(getApplicationContext());

        AudienceNetworkAds.initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}