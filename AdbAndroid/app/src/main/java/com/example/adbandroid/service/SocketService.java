package com.example.adbandroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.adbandroid.bean.MessageEvent;
import com.example.adbandroid.interfaces.SocketConnectInterface;
import com.example.adbandroid.interfaces.SocketDisableInterface;
import com.example.adbandroid.interfaces.SocketReceiveInterface;
import com.example.adbandroid.thread.AcceptThread;
import com.example.adbandroid.tool.SocketUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by gavinandre on 17-11-2.
 */

public class SocketService extends Service {
    private static final String TAG = SocketService.class.getSimpleName();
    private boolean isConnect = false;
    private static Socket socket; //这里定义了一个静态socket 供其他activity使用
    private static BufferedReader bufferedReader = null;
    private static PrintWriter printWriter = null;

    private AcceptThread mAcceptThread = null;

    private SocketReceiveInterface socketReceiveInterface;
    private SocketDisableInterface socketDisableInterface;
    private SocketConnectInterface socketConnectInterface;
    private boolean running = true;

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return new Binder();
    }

    public class Binder extends android.os.Binder {
        public SocketService getSocketService() {
            return SocketService.this;
        }
    }

    //Service被创建时调用
    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        EventBus.getDefault().register(this);
        super.onCreate();
    }

    //Service被启动时调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAcceptThread = new AcceptThread();
        mAcceptThread.start();
        return START_NOT_STICKY;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEvent(MessageEvent event) {
        switch (event.type) {
            case SocketUtils.CON: {
                String strAddress = event.message;
                Log.i(TAG, "IP:" + strAddress);
                if (socketConnectInterface != null) {
                    socketConnectInterface.onSocketConnect();
                }
                break;
            }
            case SocketUtils.REC: {
                String strRcv = event.message;
                Log.i(TAG, "Rec:" + strRcv);
                try {
                    handleResult(strRcv);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case SocketUtils.SEN: {
                if (mAcceptThread != null) {
                    final String send = event.message;
                    Log.i(TAG, "onMessageEvent: " + send);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mAcceptThread.sendMsg(send);
                        }
                    }).start();
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    //Service被关闭之前回调
    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        mAcceptThread.onStop();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void handleResult(String strRcv) throws IOException {
        if (socketReceiveInterface != null) {
            socketReceiveInterface.onSocketReceive(strRcv, 0);
        }
    }

    public static Socket getSocket() {
        return socket;
    }

    public static BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public static PrintWriter getPrintWriter() {
        return printWriter;
    }

    public void setSocketReceiveInterface(SocketReceiveInterface socketReceiveInterface) {
        this.socketReceiveInterface = socketReceiveInterface;
    }

    public void setSocketDisableInterface(SocketDisableInterface socketDisableInterface) {
        this.socketDisableInterface = socketDisableInterface;
    }

    public void setSocketConnectInterface(SocketConnectInterface socketConnectInterface) {
        this.socketConnectInterface = socketConnectInterface;
    }
}
