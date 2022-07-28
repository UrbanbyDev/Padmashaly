package com.urbantech.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.urbantech.item.ItemUser;

public class SharedPref {

    private Methods methods;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static String TAG_UID = "uid" ,TAG_USERNAME = "name", TAG_EMAIL = "email", TAG_MOBILE = "mobile", TAG_REMEMBER = "rem", TAG_IS_REPORTER = "isreporter",
            TAG_PASSWORD = "pass", SHARED_PREF_AUTOLOGIN = "autologin", TAG_DP = "dp", TAG_LOGIN_TYPE = "loginType", TAG_AUTH_ID = "auth_id", TAG_NIGHT_MODE = "nightmode";

    public  SharedPref(Context context) {
        methods = new Methods(context);
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsSelectCatShown() {
        return sharedPreferences.getBoolean("sel_cat", false);
    }

    public void setIsSelectCatShown(Boolean isshown) {
        editor.putBoolean("sel_cat", isshown);
        editor.apply();
    }

    public String getCat() {
        return sharedPreferences.getString("cat","");
    }

    public void setCat(String cat) {
        editor.putString("cat", cat);
        editor.apply();
    }

    public String getDarkMode() {
        return sharedPreferences.getString(TAG_NIGHT_MODE, Constant.DARK_MODE_SYSTEM);
    }

    public void setDarkMode(String nightMode) {
        editor.putString(TAG_NIGHT_MODE, nightMode);
        editor.apply();
    }

    public void setIsRemember(Boolean rem) {
        editor.putBoolean("rem", rem);
        editor.apply();
    }

    public String getUID() {
        return sharedPreferences.getString("uid","");
    }

    public void setUID(String cat) {
        editor.putString("uid", cat);
        editor.apply();
    }

    public String getUser() {
        return sharedPreferences.getString("user","");
    }

    public void setUser(String user) {
        editor.putString("user", user);
        editor.apply();
    }

    public void setIsFirst(Boolean flag) {
        editor.putBoolean("firstopen", flag);
        editor.apply();
    }

    public Boolean getIsFirst() {
        return sharedPreferences.getBoolean("firstopen", true);
    }

    public void setLoginDetails(ItemUser itemUser, Boolean isRemember, String password, String loginType) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_UID, methods.encrypt(itemUser.getId()));
        editor.putString(TAG_USERNAME, methods.encrypt(itemUser.getName()));
        editor.putString(TAG_MOBILE, methods.encrypt(itemUser.getMobile()));
        editor.putString(TAG_EMAIL, methods.encrypt(itemUser.getEmail()));
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_PASSWORD, methods.encrypt(password));
        editor.putString(TAG_DP, methods.encrypt(itemUser.getImage()));
        editor.putString(TAG_LOGIN_TYPE, methods.encrypt(loginType));
        editor.putString(TAG_AUTH_ID, methods.encrypt(itemUser.getAuthID()));
        editor.putBoolean(TAG_IS_REPORTER, itemUser.getIsReporter());
        editor.apply();
    }

    public void setRemeber(Boolean isRemember) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_PASSWORD, "");
        editor.apply();
    }

    public void getUserDetails() {
        Constant.itemUser = new ItemUser(
                methods.decrypt(sharedPreferences.getString(TAG_UID,"")),
                methods.decrypt(sharedPreferences.getString(TAG_USERNAME,"")),
                methods.decrypt(sharedPreferences.getString(TAG_EMAIL,"")),
                methods.decrypt(sharedPreferences.getString(TAG_MOBILE,"")),
                methods.decrypt(sharedPreferences.getString(TAG_DP,"")),
                methods.decrypt(sharedPreferences.getString(TAG_AUTH_ID,"")),
                methods.decrypt(sharedPreferences.getString(TAG_LOGIN_TYPE,"")));
                sharedPreferences.getBoolean(TAG_IS_REPORTER,false);
    }

    public String getEmail() {
        return methods.decrypt(sharedPreferences.getString(TAG_EMAIL,""));
    }

    public String getPassword() {
        return methods.decrypt(sharedPreferences.getString(TAG_PASSWORD,""));
    }

    public Boolean isRemember() {
        return sharedPreferences.getBoolean(TAG_REMEMBER, false);
    }

    public Boolean getIsNotification() {
        return sharedPreferences.getBoolean("noti", true);
    }

    public void setIsNotification(Boolean isNotification) {
        editor.putBoolean("noti", isNotification);
        editor.apply();
    }

    public Boolean getIsAutoLogin() {
        return sharedPreferences.getBoolean(SHARED_PREF_AUTOLOGIN, false);
    }

    public void setIsAutoLogin(Boolean isAutoLogin) {
        editor.putBoolean(SHARED_PREF_AUTOLOGIN, isAutoLogin);
        editor.apply();
    }

    public Boolean getIsRemember() {
        return sharedPreferences.getBoolean(TAG_REMEMBER, false);
    }

    public String getLoginType() {
        return methods.decrypt(sharedPreferences.getString(TAG_LOGIN_TYPE,""));
    }

    public String getAuthID() {
        return methods.decrypt(sharedPreferences.getString(TAG_AUTH_ID,""));
    }

    public Boolean getIsReporter() {
        return sharedPreferences.getBoolean(TAG_IS_REPORTER,false);
    }

    public void setId(String randomNumberString) {
        editor.putString("member_id",randomNumberString);
        editor.apply();
    }

    public String getMemberID(){
        return sharedPreferences.getString("member_id","");
    }
}