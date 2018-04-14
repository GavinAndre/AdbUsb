package com.example.adbandroid.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.adbandroid.R;
import com.example.adbandroid.bean.MessageEvent;
import com.example.adbandroid.interfaces.SocketConnectInterface;
import com.example.adbandroid.interfaces.SocketDisableInterface;
import com.example.adbandroid.interfaces.SocketReceiveInterface;
import com.example.adbandroid.service.SocketService;
import com.example.adbandroid.tool.SocketUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private static final String TAG = MainActivity.class.getSimpleName();

    private SocketService.Binder binder = null;
    private Intent socketIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initService();
    }

    private void initService() {
        socketIntent = new Intent();
        socketIntent.setClass(MainActivity.this, SocketService.class);
        // 启动Socket服务
        startService(socketIntent);
        bindService(socketIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (SocketService.Binder) service;
        binder.getSocketService().setSocketConnectInterface(new SocketConnectInterface() {
            @Override
            public void onSocketConnect() {
                Log.i(TAG, "onSocketConnect: ");
            }
        });
        binder.getSocketService().setSocketReceiveInterface(new SocketReceiveInterface() {
            @Override
            public void onSocketReceive(Object socketResult, int code) {
                final String msg = (String) socketResult;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        binder.getSocketService().setSocketDisableInterface(new SocketDisableInterface() {
            @Override
            public void onSocketDisable() {
                Log.i(TAG, "onSocketDisable: ");
            }
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        unbindService(this);
        stopService(socketIntent);
        super.onDestroy();
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        EventBus.getDefault().post(new MessageEvent(SocketUtils.SEN, "message from android"));
    }
}
