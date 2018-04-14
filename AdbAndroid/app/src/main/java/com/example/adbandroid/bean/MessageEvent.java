package com.example.adbandroid.bean;

/**
 * Created by gavinandre on 17-11-3.
 */

public class MessageEvent {

    public final String message;
    public final int type;

    public MessageEvent(int type, String message) {
        this.type = type;
        this.message = message;
    }
}