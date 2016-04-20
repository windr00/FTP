package com.ftpserver.network;

import com.ftpserver.commandhandler.CMDHandler;
import com.ftpserver.logger.ConsoleLogger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Created by windr on 4/18/16.
 */
public class ClientSocketThread extends Thread {

    private Socket client;
    private String userName = "";
    private String passwd = "";

    private Communication communicationInstance;

    private CMDHandler handler;

    public ClientSocketThread(Socket client) {
        communicationInstance = Communication.getInstance();
        handler = new CMDHandler(this, "onResponse");
        this.client = client;
    }

    public void onResponse(java.lang.String msg) {
        try {
            communicationInstance.send(client, msg.toCharArray());
        } catch (Exception e) {
            ConsoleLogger.info(String.valueOf(System.currentTimeMillis()));
            ConsoleLogger.error(e.toString());
            ConsoleLogger.error(e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String cmd = new String(communicationInstance.read(client));
                String op = cmd.substring(0, 4);
                ConsoleLogger.info(client.toString());
                ConsoleLogger.info(op);
                cmd = cmd.substring(4);

                Method handle = handler.getClass().getMethod(op.trim(), String.class);
                handle.invoke(handler, cmd);

                if (op.equals("QUIT")) {
                    client.close();
                    ConsoleLogger.info("CLOSE ON " + client.toString());
                    break;
                }
            }

        } catch (Exception e) {
            if (e.getClass() == IOException.class) {
                ConsoleLogger.error(e.getMessage());
            }
        }
    }
}
