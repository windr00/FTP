package com.ftpserver.agent;

import com.ftpserver.Statics;
import com.ftpserver.commandhandler.CMDHandler;
import com.ftpserver.logger.ConsoleLogger;
import com.ftpserver.network.Communication;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by windr on 4/18/16.
 */
public class ClientAgent extends Thread {

    private Socket client;
    private String userName = "";
    private String passwd = "";

    private Communication communicationInstance;

    private CMDHandler handler;

    public ClientAgent(Socket client) {
        communicationInstance = Communication.getInstance();
        handler = new CMDHandler(this, "onResponse");
        this.client = client;
    }

    public void onResponse(java.lang.String msg) throws Exception {
        communicationInstance.send(client, msg.getBytes());
    }

    private void logException(Exception e) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ConsoleLogger.error(df.format(new Date()));
        ConsoleLogger.error(e.toString());
        ConsoleLogger.error(e.getMessage());
        e.printStackTrace(System.err);
    }

    @Override
    public void run() {
        try {
            communicationInstance.send(client, Statics.INIT_RETURN.getBytes());
        } catch (Exception e) {
            handler.cleanUp();
            return;
        }
        while (true) {
            try {
                String cmd = new String(communicationInstance.read(client));
                if (Statics.SYSTEM_STASH == "\\") {
                    if (cmd.contains("/\\/")) {
                        cmd = cmd.replace("/\\/", "\\");
                    }
                }
                String[] params = cmd.split(" ");
                String op = params[0];
                op = op.toUpperCase();

                cmd = "";
                for (int i = 1; i < params.length; i++) {
                    cmd += params[i];
                    if (i != params.length - 1) {
                        cmd += " ";
                    }
                }

                try {
                    Method handle = handler.getClass().getMethod(op.trim(), String.class);

                    handle.invoke(handler, cmd);
                } catch (Exception e) {
                    logException(e);
                    try {
                        if (e.getClass() == NoSuchMethodException.class) {
                            onResponse(Statics.COMMAND_NOT_UNDERSTOOD_RETURN);
                        }
                    } catch (Exception ex) {
                        logException(ex);
                    }
                }
                if (op.equals("QUIT")) {
                    handler.cleanUp();
                    client.close();
                    ConsoleLogger.info("CLOSE ON " + client.toString());
                    break;
                }
            } catch (Exception e) {
                logException(e);

                handler.cleanUp();
                try {
                    client.close();
                } catch (IOException ex) {
                    return;

                }
                return;
            }
        }

    }
}
