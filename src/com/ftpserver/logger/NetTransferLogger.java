package com.ftpserver.logger;

import com.ftpserver.Statics;

/**
 * Created by windr on 4/19/16.
 */
public class NetTransferLogger {

    private static NetTransferLogger _instance;
    private long upsum = 0;
    private long downsum = 0;

    private NetTransferLogger() {
    }

    public static NetTransferLogger getInstance() {
        if (_instance == null) {
            _instance = new NetTransferLogger();
        }
        return _instance;
    }

    public void logNetTransfer(Integer length, Statics.NET_TRANSFER_TYPE type) {
        if (type == Statics.NET_TRANSFER_TYPE.DOWNLOAD) {
            downsum += length;
//            System.out.println();
//            System.out.println("============================================");
//            System.out.println("= LOG: " + length + " BYTES SENT THIS TIME");
//            System.out.println("= LOG: " + downsum + " BYTES IN TOTAL");
//            System.out.println("============================================");
//            System.out.println();
        } else if (type == Statics.NET_TRANSFER_TYPE.UPLOAD) {
            upsum += length;
//            System.out.println();
//            System.out.println("============================================");
//            System.out.println("= LOG: " + length + " BYTES RECVED THIS TIME");
//            System.out.println("= LOG: " + upsum + " BYTES IN TOTAL");
//            System.out.println("============================================");
//            System.out.println();
        }
    }

}
