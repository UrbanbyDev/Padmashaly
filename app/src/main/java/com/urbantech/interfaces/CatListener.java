package com.urbantech.interfaces;

import com.urbantech.item.ItemCat;

import java.util.ArrayList;

public interface CatListener {
    void onStart();

    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayList);
}