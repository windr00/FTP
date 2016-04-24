package com.ftpserver;

import com.ftpserver.agent.UserLoginAgent;
import com.ftpserver.logger.ConsoleLogger;
import com.ftpserver.logger.NetTransferLogger;
import com.ftpserver.network.ClientSocketThread;
import com.ftpserver.network.Communication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 4, 2000, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<Runnable>(4));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ConsoleLogger.info(df.format(new Date()));
        Config configInstance = Config.getInstance();
        Communication communication = Communication.getInstance();
        communication.addNetworkTransferEventListener(NetTransferLogger.getInstance(), "logNetTransfer");
        while (true) {
            if (!configInstance.init("." + Statics.SYSTEM_STASH + "ftpconfig.json")) {
                try {
                    ConsoleLogger.error("Config load falied");
                    ConsoleLogger.info("please input ftp root path");
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    configInstance.setRoot(br.readLine());
                    ConsoleLogger.info("please input command port");
                    configInstance.setCmdPort(Integer.parseInt(br.readLine()));
                    ConsoleLogger.info("please input maximum connection limit");
                    configInstance.setMaxConnection(Integer.parseInt(br.readLine()));
                    ConsoleLogger.info("please input ls cmd path");
                    configInstance.setLsCMD(br.readLine());
                    configInstance.saveSettings("." + Statics.SYSTEM_STASH + "ftpconfig.json");
                } catch (Exception e) {
                    ConsoleLogger.error("FATAL ERROR, EXITING NOW!");
                    e.printStackTrace();
                    return;
                }

            }
            break;
        }

        try {
            UserLoginAgent.getInstance().init("." + Statics.SYSTEM_STASH + "users.json");
        } catch (Exception e) {
            ConsoleLogger.error("FATAL ERROR, EXITING NOW!");
            e.printStackTrace();
            return;
        }

        try {
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
            e.printStackTrace(System.out);
        }
    }
}
