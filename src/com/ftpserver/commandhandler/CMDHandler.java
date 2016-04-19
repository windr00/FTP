package com.ftpserver.commandhandler;

import com.ftpserver.Statics;
import com.ftpserver.agent.UserLoginAgent;
import com.ftpserver.fileIO.FileIO;
import com.ftpserver.logger.ConsoleLogger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.OutputStream;
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

    private Socket pasvSocket = null;

    private String currentPath = "/";

    private FileIO fileIOInstance = FileIO.getInstance();

    private void logException(Exception e) {
        ConsoleLogger.error(String.valueOf(System.currentTimeMillis()));
        ConsoleLogger.error(e.toString());
        ConsoleLogger.error(e.getMessage());
        e.printStackTrace(System.out);
    }

    public String USER(String cmd) {
        username = cmd.trim();
        return Statics.USER_RETURN;
    }

    public String PASS(String cmd) {
        String userpass = cmd.trim();
        isloggedin = UserLoginAgent.userAuthenticate(username, userpass);
        if (isloggedin) {
            return Statics.PASS_LOGEDIN_RETURN;
        } else {
            return Statics.PASS_FAILED_RETURN;
        }
    }

    public String QUIT(String cmd) {
        return Statics.QUIT_RETURN;
    }

    public String TYPE(String cmd) {
        if (cmd.trim().equals("A")) {
            netType = Statics.TRANSFER_TYPE.ASCII;
            return Statics.TYPE_RETURN + netType.toString();
        } else if (cmd.trim().equals("I")) {
            netType = Statics.TRANSFER_TYPE.BINARY;
            return Statics.TYPE_RETURN + netType.toString();
        } else {
            return Statics.TYPE_FAILED_RETURN;
        }
    }

    public String NOOP(String cmd) {
        return Statics.NOOP_RETURN;
    }

    public String CWD(String cmd) {
        if (fileIOInstance.exist(cmd)) {
            currentPath = cmd;
            return Statics.CWD_SUCC_RETURN;
        } else {
            return Statics.CWD_FAILED_RETURN;
        }
    }

    public String PWD(String cmd) {
        return Statics.PWD_RETURN + currentPath;
    }

    public String PORT(String cmd) {
        String[] params = cmd.split(",");
        if (params.length <= 4 || params.length >= 7) {
            return Statics.PORT_FAILED_RETURN;
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
        return Statics.PORT_SUCC_RETURN;
    }

    public String PASV(String cmd) {
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

    public String RETR(String cmd) {
        try {
            String filepath = cmd.trim();
            Socket dataSocket = null;
            if (netMode == Statics.TRANSFER_MODE.PORT) {
                dataSocket = new Socket(this.portHost, this.portPort);
            } else {

            }
            BufferedReader filereader = fileIOInstance.open(filepath);
            char buffer[] = new char[Statics.FILE_READ_BUFFER_LENGTH];
            OutputStream ostream = dataSocket.getOutputStream();
            int current_length = 0;
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
            return Statics.RETR_SUCC_RETURN;
        } catch (Exception e) {
            logException(e);
            return Statics.RETR_FAILED_RETURN;
        }
    }

    public String STOR(String cmd) {
        try {

        } catch (Exception e) {

        }
    }
}
