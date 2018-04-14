package com.example.adbandroid.interfaces;

/**
 * Created by gavinandre on 17-6-26.
 */

public interface SocketReceiveInterface<T> {
    void onSocketReceive(T socketResult, int code);
}
