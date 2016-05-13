package com.ftpserver.commandhandler;

import com.ftpserver.Config;
import com.ftpserver.Statics;
import com.ftpserver.agent.UserLoginAgent;
import com.ftpserver.event.Event;
import com.ftpserver.event.EventHandler;
import com.ftpserver.exceptions.FileIsDirectoryException;
import com.ftpserver.exceptions.FileIsNotDirectoryException;
import com.ftpserver.fileIO.FileIO;
import com.ftpserver.network.Communication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;

/**
 * Created by windr on 4/19/16.
 */
public class CMDHandler {

    private String username = "";

    private String clientAddress = "";

    private boolean isloggedin = false;

    private boolean useUTF8 = true;

    private Statics.TRANSFER_TYPE netType = Statics.TRANSFER_TYPE.ASCII;

    private Statics.TRANSFER_MODE netMode;

    private String portHost;

    private int portPort;

    private ServerSocket pasvSocket = null;

    private Socket dataSocket = null;

    private String currentPath = Statics.SYSTEM_STASH;

    private String renameFile = "";

    private FileIO fileIOInstance = FileIO.getInstance();

    private Communication commInstance = Communication.getInstance();

    private UserLoginAgent userLoginAgent = UserLoginAgent.getInstance();

    private EventHandler onResponseEventHandler;

    public CMDHandler(Object obj, String mtd, String clientAddress) {
        onResponseEventHandler = new EventHandler();
        onResponseEventHandler.addEvent(new Event(obj, mtd));
        this.clientAddress = clientAddress;
    }

    public void cleanUp() {
        try {
            pasvSocket.close();
        } catch (Exception e) {
            return;
        }
    }


    private void response(String msg) throws Exception {
            onResponseEventHandler.invokeAll(msg);
    }

    public void USER(String args) throws Exception {
        username = args.trim();
        response(Statics.USER_RETURN);
    }

    public void PASS(String args) throws Exception {
        String userpass = args.trim();
        isloggedin = userLoginAgent.userAuthenticate(username, userpass);
        if (isloggedin) {
            response(Statics.PASS_LOGEDIN_RETURN);
        } else {
            response(Statics.PASS_FAILED_RETURN);
        }
    }

    public void QUIT(String args) throws Exception {
        response(Statics.QUIT_RETURN);
    }

    public void TYPE(String args) throws Exception {
        if (args.trim().equals("A")) {
            netType = Statics.TRANSFER_TYPE.ASCII;
            response(Statics.TYPE_RETURN + netType.toString() + "\n");
        } else if (args.trim().equals("I")) {
            netType = Statics.TRANSFER_TYPE.BINARY;
            response(Statics.TYPE_RETURN + netType.toString() + "\n");
        } else {
            response(Statics.TYPE_FAILED_RETURN);
        }
    }

    public void NOOP(String args) throws Exception {
        response(Statics.NOOP_RETURN);
    }

    public void CWD(String args) throws Exception {
        if (!isloggedin) {
            response(Statics.CMD_NOT_ALLOWED_RETURN);
            return;
        }
        try {
            String temp = "";
            if (!useUTF8) {
                args = new String(args.getBytes("GB2312"));
            }
            if (args.startsWith(Statics.SYSTEM_STASH)) {
                temp = fileIOInstance.cddir(fileIOInstance.appendFilePath(Config.getInstance().getRoot(), args));
            } else {
                temp = fileIOInstance.appendFilePath(Config.getInstance().getRoot(), fileIOInstance.appendFilePath(currentPath, args));
                temp = fileIOInstance.cddir(temp);
            }
            response(Statics.CWD_SUCC_RETURN);
            currentPath = temp;
        } catch (Exception e) {
            response(Statics.CWD_FAILED_RETURN);
            throw e;
        }
    }

    public void CDUP(String args) throws Exception {
        if (!isloggedin) {
            response(Statics.CMD_NOT_ALLOWED_RETURN);
            return;
        }
        try {
            if (currentPath.equals(Statics.SYSTEM_STASH)) {
                response(Statics.CDUP_SUCC_RETURN);
                return;
            }

            currentPath = fileIOInstance.appendFilePath(currentPath, ".." + Statics.SYSTEM_STASH);
            currentPath = fileIOInstance.cddir(fileIOInstance.appendFilePath(Config.getInstance().getRoot(), currentPath));
            response(Statics.CDUP_SUCC_RETURN);

        } catch (Exception e) {
            response(Statics.CDUP_FAILED_RETURN);
            throw e;
        }
    }

    public void PWD(String args) throws Exception {
        if (!isloggedin) {
            response(Statics.CMD_NOT_ALLOWED_RETURN);
            return;
        }
        if (!currentPath.startsWith(Statics.SYSTEM_STASH)) {
            currentPath = Statics.SYSTEM_STASH + currentPath;
        }
        response(Statics.PWD_RETURN + "\"" + currentPath + "\"\n");
    }

    public void SYST(String args) throws Exception {
        response(Statics.SYST_RETURN);
    }

    public void FEAT(String args) throws Exception {
        response(Statics.FEAT_RETURN);
    }

    public void OPTS(String args) throws Exception {
        args = args.toUpperCase();
        if (args.contains("UTF8")) {
            if (args.contains("ON")) {
                response(Statics.OPTS_UTF8_ON_RETURN);
                useUTF8 = true;
            } else if (args.contains("OFF")) {
                response(Statics.OPTS_UTF8_OFF_RETURN);
                useUTF8 = false;
            }
        } else if (args.contains("GB2312") && args.contains("ON")) {
            response(Statics.OPTS_UTF8_OFF_RETURN);
            useUTF8 = false;
        } else {

            response(Statics.COMMAND_NOT_UNDERSTOOD_RETURN);
        }
    }

    public void PORT(String args) throws Exception {
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

    public void AUTH(String args) throws Exception {
        response(Statics.AUTH_RETURN);
    }

    private int getUnusedPort() {

        int i = 1025;
        while (true) {
            try {
                ServerSocket temp = new ServerSocket(i);
                temp.close();
                return i;
            } catch (IOException e) {
                i++;
                continue;
            }
        }
    }

    private void setDataSocket() throws Exception {
        if (pasvSocket != null) {
            pasvSocket.setSoTimeout(0);
        }
        if (netMode == Statics.TRANSFER_MODE.PORT) {
            dataSocket = new Socket(this.portHost, this.portPort);
        } else {
            if (dataSocket == null || dataSocket.isClosed()) {
                dataSocket = pasvSocket.accept();
            }
        }
    }

    public void PASV(String args) throws Exception {
        try {
            int p = getUnusedPort();
            if (pasvSocket != null) {
                pasvSocket.close();
            }
            pasvSocket = new ServerSocket(p);
            String ip = clientAddress.replace('.', ',');
            String port = String.valueOf(p / 256) + "," + String.valueOf(p % 256);
            netMode = Statics.TRANSFER_MODE.PASV;
            response(Statics.PASV_SUCC_RETURN + ip + "," + port + ")\n");
            pasvSocket.setSoTimeout(1000);
            if (dataSocket != null) {
                dataSocket.close();
            }
            try {
                dataSocket = pasvSocket.accept();
            } catch (Exception e) {
                return;
            }
        } catch (Exception e) {
            response(Statics.PASV_FAILED_RETURN);
            throw e;
        }
    }

    private byte[] parseReturnChar(byte[] buffer, int originalLength) {
        byte ret[] = new byte[originalLength];
        int k = 0;
        for (int i = 0; i < originalLength; i++) {
            if (buffer[i] != '\r') {
                ret[k] = buffer[i];
                k++;
            }
        }
        buffer = new byte[k];
        System.arraycopy(ret, 0, buffer, 0, k);
        return buffer;
    }

    public void RETR(String args) throws Exception {
        if (!isloggedin) {
            response(Statics.CMD_NOT_ALLOWED_RETURN);
            return;
        }
        try {
            String filepath = args;
            String temp = fileIOInstance.appendFilePath(currentPath, filepath);
            temp = fileIOInstance.appendFilePath(Config.getInstance().getRoot(), temp);
            if (fileIOInstance.isDir(temp)) {
                response(Statics.RETR_FAILED_RETURN);
                return;
            }
            FileInputStream filereader = fileIOInstance.open(temp);
            byte buffer[] = new byte[Statics.FILE_READ_BUFFER_LENGTH];
            int current_length = 0;
            if (netType == Statics.TRANSFER_TYPE.ASCII) {
                response(Statics.RETR_STRART_A_RETURN);
            } else {
                response(Statics.RETR_STRART_I_RETURN);
            }
            setDataSocket();
            while ((current_length = filereader.read(buffer)) != -1) {
                if (netType == Statics.TRANSFER_TYPE.ASCII) {
                    buffer = parseReturnChar(buffer, current_length);
//                    int minus = Statics.FILE_READ_BUFFER_LENGTH - buffer.length;
                    current_length = buffer.length;

                }
                commInstance.send(dataSocket, buffer);
                buffer = new byte[Statics.FILE_READ_BUFFER_LENGTH];
            }
            filereader.close();
            dataSocket.close();
            response(Statics.RETR_SUCC_RETURN);
        } catch (Exception e) {
            response(Statics.RETR_FAILED_RETURN);
            throw e;
        }
    }

    public void STOR(String args) throws Exception {
        if (!isloggedin) {
            response(Statics.CMD_NOT_ALLOWED_RETURN);
            return;
        }
        try {
            if (!useUTF8) {
                args = new String(args.getBytes("GB2312"));
            }
            if (netType == Statics.TRANSFER_TYPE.ASCII) {
                response(Statics.STOR_STRART_A_RETURN);
            } else {
                response(Statics.STOR_STRART_I_RETURN);
            }
            setDataSocket();
            if (fileIOInstance.exist(fileIOInstance.appendFilePath(Config.getInstance().getRoot(), fileIOInstance.appendFilePath(currentPath, args)))) {
                throw new FileAlreadyExistsException(args);
            }
            InputStream istream = dataSocket.getInputStream();
            byte buffer[] = new byte[Statics.NET_READ_BUFFER_LENGTH];
            int amount = 0;
            fileIOInstance.create(fileIOInstance.appendFilePath(Config.getInstance().getRoot(), fileIOInstance.appendFilePath(currentPath, args)));
            String file = fileIOInstance.appendFilePath(Config.getInstance().getRoot(), fileIOInstance.appendFilePath(currentPath, args));
            while ((amount = istream.read(buffer)) != -1) {

                fileIOInstance.write(file, buffer, amount);
            }
            istream.close();
            //dataSocket.close();
            response(Statics.STOR_SUCC_RETURN);

        } catch (Exception e) {
            response(Statics.STOR_FAILED_RETURN);
            throw e;
        }
    }

    public void MKD(String args) throws Exception {
        if (!isloggedin) {
            response(Statics.CMD_NOT_ALLOWED_RETURN);
            return;
        }
        try {
            if (!useUTF8) {
                args = new String(args.getBytes("GB2312"));
            }
            String temp = fileIOInstance.appendFilePath(currentPath, args);
            String fullpath = fileIOInstance.appendFilePath(Config.getInstance().getRoot(), temp);
            fileIOInstance.mkDir(fullpath);
            response(Statics.MKD_SUCC_RETURN);
        } catch (Exception e) {
            if (e.getClass() == FileAlreadyExistsException.class) {
                response(Statics.MKD_EXIST_WARN_RETURN);
            } else {
                response(Statics.MKD_FAILED_RETURN);
            }
            throw e;
        }

    }

    public void RMD(String args) throws Exception {
        if (!isloggedin) {
            response(Statics.CMD_NOT_ALLOWED_RETURN);
            return;
        }
        try {
            if (!useUTF8) {
                args = new String(args.getBytes("GB2312"));
            }
            String temp = fileIOInstance.appendFilePath(currentPath, args);
            String fullpath = fileIOInstance.appendFilePath(Config.getInstance().getRoot(), temp);
            fileIOInstance.rmdir(fullpath);
            response(Statics.RMD_SUCC_RETURN);
        } catch (Exception e) {
            if (e.getClass() == FileIsNotDirectoryException.class) {
                response(Statics.RMD_ISFILE_WARN_RETURN);
            } else {
                response(Statics.RMD_FALIED_RETURN);
            }
            throw e;
        }
    }

    public void DELE(String args) throws Exception {
        if (!isloggedin) {
            response(Statics.CMD_NOT_ALLOWED_RETURN);
            return;
        }
        try {
            if (!useUTF8) {
                args = new String(args.getBytes("GB2312"));
            }
            String temp = fileIOInstance.appendFilePath(currentPath, args);
            String fullpath = fileIOInstance.appendFilePath(Config.getInstance().getRoot(), temp);
            fileIOInstance.rmfile(fullpath);
            response(Statics.DELE_SUCC_RETURN);
        } catch (Exception e) {
            if (e.getClass() == FileIsDirectoryException.class) {
                response(Statics.DELE_ISDIR_WARN_RETURN);
            } else {
                response(Statics.DELE_FALIED_RETURN);
            }
            throw e;
        }
    }


    public void ABOR(String args) throws Exception {
        if (dataSocket != null && !dataSocket.isClosed()) {
            dataSocket.close();
            response(Statics.ABOR_SUCC_RETURN);
        } else {
            response(Statics.ABOR_FAILED_RETURN);
        }
    }

    public void LIST(String args) throws Exception {
        if (!isloggedin) {
            response(Statics.CMD_NOT_ALLOWED_RETURN);
            return;
        }
        try {
            response(Statics.LIST_START_RETURN);
            setDataSocket();
            String str = fileIOInstance.lsdir(fileIOInstance.appendFilePath(Config.getInstance().getRoot(), currentPath));
            byte buffer[];
            if (useUTF8) {
                buffer = str.getBytes("UTF8");
            } else {
                buffer = str.getBytes("GB2312");
            }
            buffer = parseReturnChar(buffer, buffer.length);
            //ostream.write(buffer);
            commInstance.send(dataSocket, buffer);
            response(Statics.LIST_SUCC_RETURN);
            dataSocket.close();
        } catch (Exception e) {
            response(Statics.LIST_FAILED_RETURN);
            throw e;
        }
    }

    public void RNFR(String args) throws Exception {
        if (!isloggedin) {
            response(Statics.CMD_NOT_ALLOWED_RETURN);
            return;
        }
        try {
            if (!useUTF8) {
                args = new String(args.getBytes("GB2312"));
            }
            String temp = fileIOInstance.appendFilePath(Config.getInstance().getRoot(), fileIOInstance.appendFilePath(currentPath, args));
            if (!fileIOInstance.exist(temp)) {
                throw new FileNotFoundException(args);
            }
            renameFile = temp;
            response(Statics.RNFR_SUCC_RETURN);
        } catch (Exception e) {
            response(Statics.RNFR_FALIED_RETURN);
            throw e;
        }
    }

    public void RNTO(String args) throws Exception {
        if (!isloggedin) {
            response(Statics.CMD_NOT_ALLOWED_RETURN);
            return;
        }
        try {
            if (!useUTF8) {
                args = new String(args.getBytes("GB2312"));
            }
            String temp = fileIOInstance.appendFilePath(Config.getInstance().getRoot(), fileIOInstance.appendFilePath(currentPath, args));
            if (fileIOInstance.exist(temp)) {
                throw new FileAlreadyExistsException(temp);
            }
            fileIOInstance.rnfile(renameFile, temp);
            response(Statics.RNTO_SUCC_RETURN);
            renameFile = "";
        } catch (Exception e) {
            response(Statics.RNTO_FAILED_RETURN);
            throw e;
        }
    }

    public void SIZE(String args) throws Exception {
        if (!isloggedin) {
            response(Statics.CMD_NOT_ALLOWED_RETURN);
            return;
        }
        try {
            if (!useUTF8) {
                args = new String(args.getBytes("GB2312"));
            }
            String temp = fileIOInstance.appendFilePath(Config.getInstance().getRoot(), fileIOInstance.appendFilePath(currentPath, args));
            long size = fileIOInstance.getsize(temp);
            response(Statics.SIZE_SUCC_RETURN + String.valueOf(size) + "\n");
        } catch (Exception e) {
            response(Statics.SIZE_FAILED_RETURN);
            throw e;
        }
    }

}
