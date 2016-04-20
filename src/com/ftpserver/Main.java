package com.ftpserver;

import com.ftpserver.logger.ConsoleLogger;
import com.ftpserver.network.ClientSocketThread;
import com.ftpserver.network.Communication;

import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        //System.out.print("Hello World");
        try {
            Communication communication = Communication.getInstance();
            //communication.addNetworkTransferEventListener(NetTransferLogger.getInstance(), "logNetTransfer");
            communication.bind(1026, 3);
            while (true) {
                Socket client = communication.accept();
                communication.send(client, Statics.INIT_RETURN.toCharArray());
                ClientSocketThread thread = new ClientSocketThread(client);
                thread.start();
            }
            // write your code here
        } catch (Exception e) {
            ConsoleLogger.info(String.valueOf(System.currentTimeMillis()));
            ConsoleLogger.error(e.toString());
            ConsoleLogger.error(e.getMessage());
        }
    }
}
