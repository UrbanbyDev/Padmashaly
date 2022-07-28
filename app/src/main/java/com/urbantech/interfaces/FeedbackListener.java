package com.urbantech.interfaces;

public interface FeedbackListener {
    void onStart();
    void onEnd(String success, String registerSuccess, String message,String user_id, String feedback);
}
