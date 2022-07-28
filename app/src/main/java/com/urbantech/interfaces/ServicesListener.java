package com.urbantech.interfaces;

import com.urbantech.item.ItemServices;

import java.util.ArrayList;

public interface ServicesListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemServices> arrayList);
}
