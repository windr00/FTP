package com.ftpserver;

import com.ftpserver.logger.NetTransferLogger;
import com.ftpserver.network.Communication;

public class Main {

    public static void main(String[] args) {
        System.out.print("Hello World");

        Communication communication = Communication.getInstance();
        communication.addNetworkTransferEventListener(NetTransferLogger.getInstance(), "logNetTransfer");

        // write your code here
    }
}
