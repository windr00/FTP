package com.ftpserver.network;

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

    private int commandPort;

    public Communication() {
        this.eventHandler = new EventHandler();
    }

    public static Communication getInstance() {
        if (_instance == null) {
            _instance = new Communication();
        }
        return _instance;
    }

    public void addNetworkTransferEventListener(Object obj, String mtd) {
        Event event = new Event(obj, mtd);
        this.eventHandler.addEvent(event);
    }

    public void bind(int port, int maxConnect) throws IOException {
        this.server = new ServerSocket(port, maxConnect);
        commandPort = port;
    }


    public Socket accept() throws IOException {
        return this.server.accept();
    }

    public void send(Socket client, char[] data) throws Exception {
        OutputStream ostream = client.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ostream));
        bw.write(data);
        bw.flush();
        //bw.close();
        //ostream.close();
        ConsoleLogger.info("SEND " + client.toString() + new String(data));
        this.eventHandler.invokeAll(data.length);
    }

    public char[] read(Socket client) throws Exception {
        InputStream istream = client.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(istream));
        String str = br.readLine();
        ConsoleLogger.info("READ " + client.toString() + str);
        //br.close();
        //istream.close();
        this.eventHandler.invokeAll(str.toCharArray().length);
        return str.toCharArray();
    }
}
