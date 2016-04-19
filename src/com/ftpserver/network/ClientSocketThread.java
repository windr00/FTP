package com.ftpserver.network;

import java.net.Socket;

/**
 * Created by windr on 4/18/16.
 */
public class ClientSocketThread implements Runnable {

    private Socket client;
    private String userName = "";
    private String passwd = "";

    public ClientSocketThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {

    }
}
