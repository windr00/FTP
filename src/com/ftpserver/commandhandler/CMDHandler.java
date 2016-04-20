package com.ftpserver.commandhandler;

import com.ftpserver.Statics;
import com.ftpserver.agent.UserLoginAgent;
import com.ftpserver.event.Event;
import com.ftpserver.event.EventHandler;
import com.ftpserver.fileIO.FileIO;
import com.ftpserver.logger.ConsoleLogger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by windr on 4/19/16.
 */
public class CMDHandler {

    private String username = "";

    private boolean isloggedin = false;

    private Statics.TRANSFER_TYPE netType;

    private Statics.TRANSFER_MODE netMode;

    private String portHost;

    private int portPort;

    private ServerSocket pasvSocket = null;

    private String currentPath = "/";

    private FileIO fileIOInstance = FileIO.getInstance();

    private EventHandler onResponseEventHandler;

    public CMDHandler(Object obj, String mtd) {
        onResponseEventHandler = new EventHandler();
        onResponseEventHandler.addEvent(new Event(obj, mtd));
    }


    private void logException(Exception e) {

        ConsoleLogger.error(String.valueOf(System.currentTimeMillis()));
        ConsoleLogger.error(e.toString());
        ConsoleLogger.error(e.getMessage());
        e.printStackTrace(System.out);
    }

    private void response(String msg) {
        try {
            onResponseEventHandler.invokeAll(msg);
        } catch (Exception e) {
            logException(e);
        }
    }

    public void USER(String args) {
        username = args.trim();
        response(Statics.USER_RETURN);
    }

    public void PASS(String args) {
        String userpass = args.trim();
        isloggedin = UserLoginAgent.userAuthenticate(username, userpass);
        if (isloggedin) {
            response(Statics.PASS_LOGEDIN_RETURN);
        } else {
            response(Statics.PASS_FAILED_RETURN);
        }
    }

    public void QUIT(String args) {
        response(Statics.QUIT_RETURN);
    }

    public void TYPE(String args) {
        if (args.trim().equals("A")) {
            netType = Statics.TRANSFER_TYPE.ASCII;
            response(Statics.TYPE_RETURN + netType.toString());
        } else if (args.trim().equals("I")) {
            netType = Statics.TRANSFER_TYPE.BINARY;
            response(Statics.TYPE_RETURN + netType.toString());
        } else {
            response(Statics.TYPE_FAILED_RETURN);
        }
    }

    public void NOOP(String args) {
        response(Statics.NOOP_RETURN);
    }

    public void CWD(String args) {
        if (fileIOInstance.exist(args)) {
            currentPath = args;
            response(Statics.CWD_SUCC_RETURN);
        } else {
            response(Statics.CWD_FAILED_RETURN);
        }
    }

    public void PWD(String args) {
        response(Statics.PWD_RETURN + currentPath);
    }

    public void PORT(String args) {
        String[] params = args.split(",");
        if (params.length <= 4 || params.length >= 7) {
            response(Statics.PORT_FAILED_RETURN);
        }
        netMode = Statics.TRANSFER_MODE.PORT;
        this.portHost = params[0] + "." + params[1] + "." + params[2] + "." + params[3];
        String portH, portL;
        if (params.length == 6) {
            portH = params[4];
            portL = params[5];
        } else {
            portH = "0";
            portL = params[4];
        }
        portPort = Integer.parseInt(portH) * 256 + Integer.parseInt(portL);
        response(Statics.PORT_SUCC_RETURN);
    }

    public void PASV(String args) {
        throw new NotImplementedException();
    }

    private byte[] parseReturnChar(byte[] buffer) {
        String str = new String(buffer);
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] == '\r') {
                str = str.substring(0, i - 1) + str.substring(i + 1);
            }
        }
        return str.getBytes();
    }

    public void RETR(String args) {
        try {
            String filepath = args.trim();
            Socket dataSocket = null;
            if (netMode == Statics.TRANSFER_MODE.PORT) {
                dataSocket = new Socket(this.portHost, this.portPort);
            } else {
                dataSocket = pasvSocket.accept();
            }
            BufferedReader filereader = fileIOInstance.open(filepath);
            char buffer[] = new char[Statics.FILE_READ_BUFFER_LENGTH];
            OutputStream ostream = dataSocket.getOutputStream();
            int current_length = 0;
            if (netType == Statics.TRANSFER_TYPE.ASCII) {
                response(Statics.RETR_STRART_A_RETURN);
            } else {
                response(Statics.RETR_STRART_I_RETURN);
            }
            while ((current_length = filereader.read(buffer)) != -1) {
                byte b[] = new String(buffer).getBytes();
                if (netType == Statics.TRANSFER_TYPE.ASCII) {
                    b = parseReturnChar(b);
                    int minus = Statics.FILE_READ_BUFFER_LENGTH - b.length;
                    current_length -= minus;

                }
                ostream.write(b, 0, current_length);
            }
            ostream.close();
            filereader.close();
            dataSocket.close();
            response(Statics.RETR_SUCC_RETURN);
        } catch (Exception e) {
            logException(e);
            response(Statics.RETR_FAILED_RETURN);
        }
    }

    public void STOR(String args) {
        args = args.trim();
        try {
            Socket dataSocket = null;
            if (netMode == Statics.TRANSFER_MODE.PORT) {
                dataSocket = new Socket(portHost, portPort);
            } else {
                dataSocket = pasvSocket.accept();
            }

            InputStream istream = dataSocket.getInputStream();
            byte buffer[] = new byte[Statics.NET_READ_BUFFER_LENGTH];
            int amount = 0;
            if (netType == Statics.TRANSFER_TYPE.ASCII) {
                response(Statics.STOR_STRART_A_RETURN);
            } else {
                response(Statics.STOR_STRART_I_RETURN);
            }
            while ((amount = istream.read(buffer)) != -1) {

                fileIOInstance.write(args, new String(buffer).toCharArray(), amount);
            }
            istream.close();
            dataSocket.close();
            response(Statics.STOR_SUCC_RETURN);

        } catch (Exception e) {
            logException(e);
            response(Statics.STOR_FAILED_RETURN);
        }
    }

    public void LIST(String args) {

    }
}
