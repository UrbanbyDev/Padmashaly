package com.urbantech.interfaces;

public interface SocialLoginListener {
    void onStart();
    void onEnd(String success, String registerSuccess, String message, String user_id, String user_name, String email, String user_profile, String user_cat, String auth_id, Boolean isReporter);
}