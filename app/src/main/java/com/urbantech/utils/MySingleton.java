package com.urbantech.utils;

import com.urbantech.item.ItemNews;

import java.util.ArrayList;

public class MySingleton {
    private static final MySingleton SELF = new MySingleton();

    private ArrayList<ItemNews> newsTemp = new ArrayList<>();
    private ArrayList<ItemNews> news = new ArrayList<>();
    private boolean isQoutes;

    private MySingleton() {
        // Don't want anyone else constructing the singleton.
    }

    public static MySingleton getInstance() {
        return SELF;
    }

    public ArrayList<ItemNews> getNewsTemp() {
        return newsTemp;
    }

    public ArrayList<ItemNews> getNews() {
        return news;
    }
}