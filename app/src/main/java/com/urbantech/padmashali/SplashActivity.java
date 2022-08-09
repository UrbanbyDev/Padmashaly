package com.urbantech.padmashali;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OneSignal;
import com.urbantech.AsyncTask.LoadAbout;
import com.urbantech.AsyncTask.LoadLogin;
import com.urbantech.interfaces.AboutListener;
import com.urbantech.interfaces.LoginListener;
import com.urbantech.item.ItemUser;
import com.urbantech.utils.Constant;
import com.urbantech.utils.DBHelper;
import com.urbantech.utils.Methods;
import com.urbantech.utils.SharedPref;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Methods methods;
    DBHelper dbHelper;
    ProgressBar progressbar_login;
    Uri data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        data = getIntent().getData();
        if (data != null) {
            String[] strings = data.toString().split("news_id=");
            Constant.pushType = "news";
            Constant.pushPostID = strings[strings.length - 1];
        }

        hideStatusBar();

        sharedPref = new SharedPref(SplashActivity.this);
        methods = new Methods(this);
        dbHelper = new DBHelper(this);

        progressbar_login = findViewById(R.id.progressbar_login);

        methods.checkPer();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPref.getIsFirst()) {
                    loadAboutData();
                } else {
                    try {
                        dbHelper.getAbout();
                        boolean isFromPush = getIntent().getExtras().getBoolean("ispushnoti", false);
                        if (isFromPush) {
                            Constant.pushPostID = getIntent().getStringExtra("id");
                            Constant.pushTitle = getIntent().getStringExtra("name");
                            Constant.pushType = getIntent().getStringExtra("type");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!sharedPref.getIsAutoLogin()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openMainActivity();
                            }
                        }, 2000);
                    } else {
                        if (methods.isNetworkAvailable()) {
                            if (sharedPref.getLoginType().equals(Constant.LOGIN_TYPE_FB)) {
                                if (AccessToken.getCurrentAccessToken() != null) {
                                    loadLogin(Constant.LOGIN_TYPE_FB, sharedPref.getAuthID());
                                } else {
                                    sharedPref.setIsAutoLogin(false);
                                    openMainActivity();
                                }
                            } else if (sharedPref.getLoginType().equals(Constant.LOGIN_TYPE_GOOGLE)) {
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (currentUser != null) {
                                    loadLogin(Constant.LOGIN_TYPE_GOOGLE, sharedPref.getAuthID());
                                } else {
                                    sharedPref.setIsAutoLogin(false);
                                    openMainActivity();
                                }
                            } else {
                                loadLogin(Constant.LOGIN_TYPE_NORMAL, "");
                            }
                        } else {
                            openMainActivity();
                        }
                    }
                }
            }
        }, 500);
    }

    private void loadAboutData() {
        if (methods.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(SplashActivity.this, new AboutListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1") && !verifyStatus.equals("-2")) {
                            String version = "";
                            try {
                                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                version = String.valueOf(pInfo.versionCode);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            if(Constant.showUpdateDialog && !Constant.appVersion.equals(version)) {
                                methods.showUpdateAlert(Constant.appUpdateMsg, true);
                            } else {
                                dbHelper.addtoAbout();
                                openLoginActivity();
                            }
                        } else if (verifyStatus.equals("-2")) {
                            methods.getInvalidUserDialog(message);
                        } else {
                            errorDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        errorDialog(getString(R.string.server_error), getString(R.string.server_error));
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_APP_DETAILS, 0, "", "", "", "", "", "", "", "", "", "", "", "",  "","","","","","","","","","","","","","","","","","","","","",null, null));
            loadAbout.execute();
        } else {
            errorDialog(getString(R.string.err_internet_not_conn), getString(R.string.err_connect_net_try));
        }
    }

    private void loadLogin(String loginType, String authID) {
        LoadLogin loadLogin = new LoadLogin(new LoginListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name, String dp, String user_cat, Boolean isReporter) {
                if (success.equals("1")) {
                    if (loginSuccess.equals("1")) {
                        Constant.itemUser = new ItemUser(user_id, user_name, sharedPref.getEmail(), "", dp, "", loginType, isReporter);
                        sharedPref.setCat(user_cat);
                        Constant.isLogged = true;

                        OneSignal.sendTag("user_id", user_id);
//                        methods.showToast(getString(R.string.login_success));
                    } else {
                        OneSignal.sendTag("user_id", "");
                    }
                    openMainActivity();
                } else {
                    openLoginActivity();
                }
            }

        }, methods.getAPIRequest(Constant.METHOD_LOGIN, 0, "", "", "", loginType, "", "", sharedPref.getEmail(), sharedPref.getPassword(), "", "", authID, "",  "","","","","","","","","","","","","","","","","","","","","",null, null));
        loadLogin.execute();
    }

    private void openLoginActivity() {
        Intent intent;
        if (sharedPref.getIsFirst()) {
            sharedPref.setIsFirst(false);
            intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void openMainActivity() {
        Intent intent;
        if (Constant.pushType.equals("news")) {
            intent = new Intent(SplashActivity.this, NewsDetailsActivity.class);
            intent.putExtra("isuser", false);
        } else if (Constant.pushType.equals("category")) {
            intent = new Intent(SplashActivity.this, NewsByCatActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.AlertDialogTheme);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        if (title.equals(getString(R.string.err_internet_not_conn)) || title.equals(getString(R.string.server_error))) {
            alertDialog.setNegativeButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadAboutData();
                }
            });
        }

        alertDialog.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}