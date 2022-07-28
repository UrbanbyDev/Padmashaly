package com.urbantech.padmashali;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.onesignal.OneSignal;
import com.urbantech.interfaces.AdConsentListener;
import com.urbantech.utils.AdConsent;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;
import com.urbantech.utils.SharedPref;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.DecimalFormat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SettingActivity extends AppCompatActivity {

    Toolbar toolbar;
    SharedPref sharedPref;
    Methods methods;
//    AdConsent adConsent;
    LinearLayout ll_consent, ll_adView, ll_clearcache;
    ConstraintLayout ll_theme;
    SwitchCompat  switch_noti;
    Boolean isNoti = true, isLoaded = false;
    TextView tv_privacy, tv_about, tv_cat, tv_cachesize, tv_moreapp, tv_rateapp, tv_theme;
    ImageView iv_theme;
    View view_settings, view_moreapp;
    ProgressDialog progressDialog;
    String them_mode = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        progressDialog = new ProgressDialog(SettingActivity.this);
        progressDialog.setMessage(getString(R.string.clearing_cache));

        isNoti = sharedPref.getIsNotification();
        them_mode = methods.getDarkMode();

        toolbar = this.findViewById(R.id.toolbar_setting);
        toolbar.setTitle(getString(R.string.settings));
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        adConsent = new AdConsent(this, new AdConsentListener() {
//            @Override
//            public void onConsentUpdate() {
//                setConsentSwitch();
//            }
//        });

        ll_theme = findViewById(R.id.ll_theme);
        ll_clearcache = findViewById(R.id.ll_cache);
//        ll_consent = findViewById(R.id.ll_consent);
        switch_noti = findViewById(R.id.switch_noti);
//        switch_consent = findViewById(R.id.switch_consent);
        tv_theme = findViewById(R.id.tv_theme);
        tv_cachesize = findViewById(R.id.tv_cachesize);
        tv_cat = findViewById(R.id.textView_manage_cat);
        tv_about = findViewById(R.id.textView_about);
        tv_privacy = findViewById(R.id.textView_privacy);
        tv_rateapp = findViewById(R.id.tv_rateapp);
        tv_moreapp = findViewById(R.id.tv_moreapp);
//        ll_adView = findViewById(R.id.ll_adView_settings);
        view_settings = findViewById(R.id.view_settings);
//        view_consent = findViewById(R.id.view_consent);
        view_moreapp = findViewById(R.id.view_moreapp);
        iv_theme = findViewById(R.id.iv_theme);
//        methods.showBannerAd(ll_adView);

        if (getString(R.string.play_more_apps).equals("")) {
            view_moreapp.setVisibility(View.GONE);
            tv_moreapp.setVisibility(View.GONE);
        }

        initializeCache();

//        if (adConsent.isUserFromEEA()) {
//            setConsentSwitch();
//        } else {
//            ll_consent.setVisibility(View.GONE);
//            view_consent.setVisibility(View.GONE);
//        }
        if (isNoti) {
            switch_noti.setChecked(true);
        } else {
            switch_noti.setChecked(false);
        }
        if (methods.isDarkMode()) {
            iv_theme.setImageResource(R.drawable.mode_dark);
        } else {
            iv_theme.setImageResource(R.drawable.mode_icon);
        }

        String mode = methods.getDarkMode();
        switch (mode) {
            case Constant.DARK_MODE_SYSTEM:
                tv_theme.setText(getString(R.string.system_default));
                break;
            case Constant.DARK_MODE_OFF:
                tv_theme.setText(getString(R.string.light));
                break;
            case Constant.DARK_MODE_ON:
                tv_theme.setText(getString(R.string.dark));
                break;
        }

        switch_noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OneSignal.setSubscription(isChecked);
                sharedPref.setIsNotification(isChecked);
            }
        });

//        switch_consent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    ConsentInformation.getInstance(SettingActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);
//                } else {
//                    ConsentInformation.getInstance(SettingActivity.this).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
//                }
//            }
//        });

        tv_rateapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String appName = getPackageName();//your application package name i.e play store application url
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id="
                                    + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + appName)));
                }
            }
        });

        tv_moreapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
            }
        });

        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, TermsOfUseActivity.class);
                intent.putExtra("isprivacy", true);
                startActivity(intent);
            }
        });

        tv_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SelectCategoriesActivity.class);
                intent.putExtra("from", getString(R.string.setting));
                startActivity(intent);
            }
        });

//        ll_consent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                adConsent.requestConsent();
//            }
//        });

        ll_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThemeDialog();
            }
        });

        if (Constant.isLogged) {
            view_settings.setVisibility(View.VISIBLE);
            tv_cat.setVisibility(View.VISIBLE);
        }

        ll_clearcache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<String, String, String>() {
                    @Override
                    protected void onPreExecute() {
                        progressDialog.show();
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(String... strings) {
                        FileUtils.deleteQuietly(getCacheDir());
                        FileUtils.deleteQuietly(getExternalCacheDir());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show();
                        tv_cachesize.setText("0 MB");
                        super.onPostExecute(s);
                    }
                }.execute();
            }
        });

        isLoaded = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void openThemeDialog() {
        final Dialog dialog = new Dialog(SettingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_theme);
        if (getString(R.string.isRTL).equals("true")) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup_them);
        MaterialButton btn_ok = dialog.findViewById(R.id.textView_ok_them);
        MaterialButton btn_cancel = dialog.findViewById(R.id.textView_cancel_them);

        String mode = methods.getDarkMode();
        assert mode != null;
        switch (mode) {
            case Constant.DARK_MODE_SYSTEM:
                radioGroup.check(radioGroup.getChildAt(0).getId());
                break;
            case Constant.DARK_MODE_OFF:
                radioGroup.check(radioGroup.getChildAt(1).getId());
                break;
            case Constant.DARK_MODE_ON:
                radioGroup.check(radioGroup.getChildAt(2).getId());
                break;
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                MaterialRadioButton rb = group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    if (checkedId == R.id.radioButton_system_them) {
                        them_mode = Constant.DARK_MODE_SYSTEM;
                    } else if (checkedId == R.id.radioButton_light_them) {
                        them_mode = Constant.DARK_MODE_OFF;
                    } else if (checkedId == R.id.radioButton_dark_them) {
                        them_mode = Constant.DARK_MODE_ON;
                    }
                }
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setDarkMode(them_mode);
                switch (them_mode) {
                    case Constant.DARK_MODE_SYSTEM:
                        tv_theme.setText(getResources().getString(R.string.system_default));
                        break;
                    case Constant.DARK_MODE_OFF:
                        tv_theme.setText(getResources().getString(R.string.light));
                        break;
                    case Constant.DARK_MODE_ON:
                        tv_theme.setText(getResources().getString(R.string.dark));
                        break;
                    default:
                        break;
                }
                dialog.dismiss();

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

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

//    private void setConsentSwitch() {
//        if (ConsentInformation.getInstance(this).getConsentStatus() == ConsentStatus.PERSONALIZED) {
//            switch_consent.setChecked(true);
//        } else {
//            switch_consent.setChecked(false);
//        }
//    }

    private void initializeCache() {
        long size = 0;
        size += getDirSize(this.getCacheDir());
        size += getDirSize(this.getExternalCacheDir());
        tv_cachesize.setText(readableFileSize(size));
    }

    public long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}