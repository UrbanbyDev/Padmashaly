package com.urbantech.interfaces;

import com.urbantech.item.ItemMember;
import com.urbantech.item.ItemSurvey;

import java.util.ArrayList;

public interface MembershipListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemMember> arrayList);
}
