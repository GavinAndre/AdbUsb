package com.example.adbandroid.tool;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by gavinandre on 17-2-23.
 */

public class SocketUtils {

    private static final String TAG = SocketUtils.class.getSimpleName();

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 10010;
    public static String VIDEOHOST = "192.168.0.166";

    public final static int CON = 0x400 + 0;
    public final static int REC = 0x400 + 1;
    public final static int SEN = 0x400 + 2;
    public final static int FIN = 0x400 + 4;

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final SocketUtils INSTANCE = new SocketUtils();
    }

    //获取单例
    public static SocketUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public String getIP() {
        String ip = null;
        try {
            Enumeration en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) en.nextElement();
                if (ni.isVirtual() || "virbr0".equals(ni.getName())) {
                    continue;
                }
                Enumeration<InetAddress> ee = ni.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress ia = ee.nextElement();
                    if (!ia.isLoopbackAddress() &&
                            !(ia instanceof Inet6Address)
                            ) {
                        ip = ia.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }
}
