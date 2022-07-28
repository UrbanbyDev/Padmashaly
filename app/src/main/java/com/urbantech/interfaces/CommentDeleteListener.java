package com.urbantech.interfaces;

import com.urbantech.item.ItemComment;

public interface CommentDeleteListener {
    void onStart();
    void onEnd(String success, String isDeleted, String message, ItemComment itemComment);
}