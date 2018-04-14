package com.example.adbpc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SendADBThread extends Thread {

    private Socket socket = null;
    OutputStream outStream = null;

    public SendADBThread(Socket socket) {
        super();
        this.socket = socket;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            outStream = socket.getOutputStream();
            String str = new Scanner(System.in).nextLine();
            while (!str.equals("no")) {
                outStream.write(str.getBytes("UTF-8"));
                str = new Scanner(System.in).nextLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
