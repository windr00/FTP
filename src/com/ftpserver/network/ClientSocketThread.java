package com.ftpserver.network;

import com.ftpserver.logger.ConsoleLogger;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by windr on 4/18/16.
 */
public class ClientSocketThread implements Runnable {

    private Socket client;
    private String userName = "";
    private String passwd = "";

    private Communication communicationInstance;

    public ClientSocketThread(Socket client) {
        communicationInstance = Communication.getInstance();
        this.client = client;
    }

    @Override
    public void run() {
        try {
            char[] str = communicationInstance.read(client);
        } catch (Exception e) {
            if (e.getClass() == IOException.class) {
                ConsoleLogger.error(e.getMessage());
            }
        }
    }
}
