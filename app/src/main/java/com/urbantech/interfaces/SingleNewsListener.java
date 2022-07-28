package com.urbantech.interfaces;

import com.urbantech.item.ItemComment;
import com.urbantech.item.ItemNews;

import java.util.ArrayList;

public interface SingleNewsListener {
    void onStart();

    void onEnd(String success, ItemNews itemNews, ArrayList<ItemComment> arrayListComments, ArrayList<ItemNews> arrayListRelated);
}