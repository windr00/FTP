package com.ftpserver.logger;

import com.ftpserver.Statics;

/**
 * Created by windr on 4/19/16.
 */
public class NetTransferLogger {

    private static NetTransferLogger _instance;
    private double upsum = 0;
    private double downsum = 0;

    private NetTransferLogger() {
    }

    public static NetTransferLogger getInstance() {
        return _instance;
    }

    public void logNetTransfer(long length, Statics.NET_TRANSFER_TYPE type) {
        if (type == Statics.NET_TRANSFER_TYPE.DOWNLOAD) {
            downsum += length;
        } else if (type == Statics.NET_TRANSFER_TYPE.UPLOAD) {
            upsum += length;
        }
    }

}
