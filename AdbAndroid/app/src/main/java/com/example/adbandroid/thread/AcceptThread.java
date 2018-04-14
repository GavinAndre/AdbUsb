package com.example.adbandroid.thread;

import android.util.Log;

import com.example.adbandroid.bean.MessageEvent;
import com.example.adbandroid.tool.SocketUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class AcceptThread extends Thread {
    private static final String TAG = AcceptThread.class.getSimpleName();
    private Socket clientSocket = null;
    private ServerSocket mServerSocket = null;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private boolean stopFlag = false;
    ReceiveThread mReceiveThread = null;

    @Override
    public void run() {
        try {
            // 实例化ServerSocket对象并设置端口号为 10010
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
            mServerSocket.bind(new InetSocketAddress(10010));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (!stopFlag) {
            if (mServerSocket != null) {
                try {
                    // 等待客户端的连接（阻塞）
                    clientSocket = mServerSocket.accept();
                    inputStream = clientSocket.getInputStream();
                    outputStream = clientSocket.getOutputStream();

                    if (clientSocket != null) {
                        mReceiveThread = new ReceiveThread(inputStream);
                        // 开启接收线程
                        mReceiveThread.start();
                        EventBus.getDefault().post(new MessageEvent(SocketUtils.CON, SocketUtils.HOST));
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block

                    e.printStackTrace();
                }

            }
        }
        Log.i(TAG, "AcceptThread is stop");
        closeSocket();
    }

    private void closeSocket() {
        try {
            if (mServerSocket != null && !mServerSocket.isClosed()) {
                mServerSocket.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String str) {

        if (clientSocket != null && outputStream != null) {
            try {
                outputStream.write(str.getBytes("UTF-8"));

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    public void onStop() {
        Log.i(TAG, "onStop: ");
        if (mReceiveThread != null) {
            mReceiveThread.onStop();
        }
        stopFlag = true;
        closeSocket();
    }

}
