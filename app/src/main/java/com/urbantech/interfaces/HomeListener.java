package com.urbantech.interfaces;

import com.urbantech.item.ItemCat;
import com.urbantech.item.ItemNews;

import java.util.ArrayList;

public interface HomeListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemNews> arrayListLatest, ArrayList<ItemNews> arrayListTrending, ArrayList<ItemNews> arrayListTop, ArrayList<ItemCat> arrayListCat);
}