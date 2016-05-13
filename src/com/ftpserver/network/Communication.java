package com.ftpserver.network;

import com.ftpserver.Statics;
import com.ftpserver.event.Event;
import com.ftpserver.event.EventHandler;
import com.ftpserver.logger.ConsoleLogger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by windr on 4/18/16.
 */
public class Communication {

    private static Communication _instance;

    private EventHandler eventHandler;

    private ServerSocket server;

    private Communication() {
        this.eventHandler = new EventHandler();
    }

    public static Communication getInstance() {
        if (_instance == null) {
            _instance = new Communication();
        }
        return _instance;
    }

    public String getHostAddress() {
        return server.getInetAddress().getHostAddress();
    }

    public void addNetworkTransferEventListener(Object obj, String mtd) {
        Event event = new Event(obj, mtd);
        this.eventHandler.addEvent(event);
    }

    public void bind(int port, int maxConnect) throws IOException {
        this.server = new ServerSocket(port, maxConnect);
    }


    public Socket accept() throws IOException {
        return this.server.accept();
    }

    public void send(Socket client, byte[] data) throws Exception {
        OutputStream ostream = client.getOutputStream();
        ostream.write(data);
        if (data.length < 1024) {
            ConsoleLogger.info("SEND " + client.toString() + new String(data));
        }
        this.eventHandler.invokeAll(data.length, Statics.NET_TRANSFER_TYPE.UPLOAD);
    }

    public byte[] read(Socket client) throws Exception {
        InputStream istream = client.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(istream));
        String str = br.readLine();
        if (str.length() < 1024) {
            ConsoleLogger.info("READ " + client.toString() + str);
        }
        this.eventHandler.invokeAll(str.getBytes().length, Statics.NET_TRANSFER_TYPE.DOWNLOAD);
        return str.getBytes();
    }
}
