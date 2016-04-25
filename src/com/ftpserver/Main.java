package com.ftpserver;

import com.ftpserver.agent.ClientAgent;
import com.ftpserver.agent.SafePassAgent;
import com.ftpserver.agent.UserLoginAgent;
import com.ftpserver.logger.ConsoleLogger;
import com.ftpserver.logger.NetTransferLogger;
import com.ftpserver.network.Communication;

import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ConsoleLogger.info(df.format(new Date()));
        Config configInstance = Config.getInstance();
        Communication communication = Communication.getInstance();
        communication.addNetworkTransferEventListener(NetTransferLogger.getInstance(), "logNetTransfer");
        try {
            configInstance.init("." + Statics.SYSTEM_STASH + "ftpconfig.json");
        } catch (Exception e) {
            ConsoleLogger.error("\"ftpconfig.json\" DOESN'T EXIST OR DAMAGED");
            ConsoleLogger.info("Please use config tool to edit server configuration");
            e.printStackTrace();
            return;
        }

        try {
            UserLoginAgent.getInstance().init("." + Statics.SYSTEM_STASH + "users.json");
        } catch (Exception e) {
            ConsoleLogger.error("\"user.json\" DOESN'T EXIST OR DAMAGED");
            ConsoleLogger.info("Please use config tool to edit server configuration");
            e.printStackTrace();
            return;
        }

        try {
            SafePassAgent.getInstance().init("." + Statics.SYSTEM_STASH + "iprestrict.json");
        } catch (Exception e) {
            ConsoleLogger.error("\"iprestrict.json\" DOESN'T EXIST OR DAMAGED");
            ConsoleLogger.error("Please use config tool to edit server configuration");
            e.printStackTrace(System.err);
            return;
        }

        try {
            communication.bind(configInstance.getCmdPort(), configInstance.getMaxConnection());
            ConsoleLogger.info("FTP Service started on " + "\"" + configInstance.getRoot() + "\"@" + InetAddress.getLocalHost().getHostAddress() + ":" + configInstance.getCmdPort());
        } catch (Exception e) {
            ConsoleLogger.error(df.format(new Date()));
            ConsoleLogger.error(e.toString());
            ConsoleLogger.error(e.getMessage());
            e.printStackTrace(System.err);
            ConsoleLogger.error("FTP Service init failed");
        }

        try {
            ExecutorService fixedPool = Executors.newFixedThreadPool(configInstance.getMaxConnection());
            while (true) {
                Socket client = communication.accept();
                String ip = client.getInetAddress().toString().substring(1);
                if (SafePassAgent.getInstance().isAllowed(ip)) {
                    ClientAgent thread = new ClientAgent(client);
                    fixedPool.execute(thread);
                } else {
                    communication.send(client, "421 IP NOT ALLOWED".getBytes());
                    client.close();
                }
            }
        } catch (Exception e) {
            ConsoleLogger.error(df.format(new Date()));
            ConsoleLogger.error(e.toString());
            ConsoleLogger.error(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
