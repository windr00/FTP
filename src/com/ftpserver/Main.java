package com.ftpserver;

import com.ftpserver.logger.ConsoleLogger;
import com.ftpserver.network.ClientSocketThread;
import com.ftpserver.network.Communication;

import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ConsoleLogger.info(df.format(new Date()));
        Config configInstance = Config.getInstance();
        Communication communication = Communication.getInstance();
        //communication.addNetworkTransferEventListener(NetTransferLogger.getInstance(), "logNetTransfer");
        try {
            configInstance.init("/Users/windr/Desktop/ls.json");

            //Config.getInstance().saveSettings("/Users/windr/Desktop/ls.json");

            communication.bind(configInstance.getCmdPort(), configInstance.getMaxConnection());
            ConsoleLogger.info("FTP Service started on " + "\"" + configInstance.getRoot() + "\"@" + InetAddress.getLocalHost().getHostAddress() + ":" + configInstance.getCmdPort());

        } catch (Exception e) {
            ConsoleLogger.error(df.format(new Date()));
            ConsoleLogger.error(e.toString());
            ConsoleLogger.error(e.getMessage());
            e.printStackTrace();
            ConsoleLogger.error("FTP Service init failed");
        }
        //System.out.print("Hello World");
        try {
            while (true) {
                Socket client = communication.accept();
                communication.send(client, Statics.INIT_RETURN.getBytes());
                ClientSocketThread thread = new ClientSocketThread(client);
                thread.start();
            }
            // write your code here
        } catch (Exception e) {
            ConsoleLogger.error(df.format(new Date()));
            ConsoleLogger.error(e.toString());
            ConsoleLogger.error(e.getMessage());
        }
    }
}
