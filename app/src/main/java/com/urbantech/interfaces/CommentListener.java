package com.urbantech.interfaces;

import com.urbantech.item.ItemComment;

import java.util.ArrayList;

public interface CommentListener {
    void onStart();

    void onEnd(String success, ArrayList<ItemComment> arrayListComment);
}