package com.urbantech.interfaces;

import com.urbantech.item.ItemNews;

import java.util.ArrayList;

public interface NewsListener {
    void onStart();

    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemNews> arrayList, int totalNews);
}