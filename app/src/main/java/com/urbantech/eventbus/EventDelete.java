package com.urbantech.eventbus;

public class EventDelete {
    private int position;

    public EventDelete(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}