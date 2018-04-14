package com.example.adbandroid.thread;

import android.util.Log;

import com.example.adbandroid.bean.MessageEvent;
import com.example.adbandroid.tool.SocketUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ReceiveThread extends Thread {
    private static final String TAG = ReceiveThread.class.getSimpleName();
    private InputStream mInputStream = null;
    private byte[] buf = new byte[512];
    private boolean stop = false;
    private String str = null;

    ReceiveThread(InputStream in) {
        // 获得输入流
        mInputStream = in;
    }

    @Override
    public void run() {
        while (!stop) {

            // 读取输入的数据(阻塞读)
            Arrays.fill(buf, (byte) 0);
            try {
                mInputStream.read(buf);
                str = new String(buf, "UTF-8").trim();
                if ("".equals(str))
                    stop = true;
                EventBus.getDefault().post(new MessageEvent(SocketUtils.REC, str));
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                stop = true;
                try {
                    mInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                e1.printStackTrace();
            }

        }
        Log.i(TAG, "ReceiveThread is stop");
    }

    public void onStop() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        stop = true;
    }
}