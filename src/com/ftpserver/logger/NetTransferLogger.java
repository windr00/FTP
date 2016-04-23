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
        if (_instance == null) {
            _instance = new NetTransferLogger();
        }
        return _instance;
    }

    public void logNetTransfer(Integer length, Statics.NET_TRANSFER_TYPE type) {
        if (type == Statics.NET_TRANSFER_TYPE.DOWNLOAD) {
            downsum += length;
        } else if (type == Statics.NET_TRANSFER_TYPE.UPLOAD) {
            upsum += length;
        }
    }

}
