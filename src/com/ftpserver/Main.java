package com.ftpserver;

import com.ftpserver.logger.ConsoleLogger;
import com.ftpserver.network.ClientSocketThread;
import com.ftpserver.network.Communication;

import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 4, 2000, TimeUnit.MICROSECONDS, new java.util.concurrent.ArrayBlockingQueue(4));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ConsoleLogger.info(df.format(new Date()));
        try {
            //Config.getInstance().init("/Users/windr/Desktop/");
            ConsoleLogger.info("FTP Service started on " + InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            ConsoleLogger.error("FTP Service init failed");
        }
        //System.out.print("Hello World");
        try {
            Communication communication = Communication.getInstance();
            //communication.addNetworkTransferEventListener(NetTransferLogger.getInstance(), "logNetTransfer");
            communication.bind(2120, 3);
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
