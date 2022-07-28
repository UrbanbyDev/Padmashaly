package com.urbantech.interfaces;

public interface ChannelListener {
    void onStart();

    void onEnd(String success, String verifyStatus, String message, String name, String desc, String url, String type);
}