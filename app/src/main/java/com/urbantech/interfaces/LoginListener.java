package com.urbantech.interfaces;

public interface LoginListener {
    void onStart();
    void onEnd(String success, String loginSuccess, String message, String user_id, String user_name, String dp, String user_cat, Boolean isReporter);
}