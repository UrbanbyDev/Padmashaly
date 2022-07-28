package com.urbantech.interfaces;

import com.urbantech.item.ItemCat;
import com.urbantech.item.ItemSurvey;

import java.util.ArrayList;

public interface SurveyDetailsListener {

    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemSurvey> arrayList);
}
