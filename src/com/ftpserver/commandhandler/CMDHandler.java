package com.ftpserver.commandhandler;

import com.ftpserver.Statics;
import com.ftpserver.agent.UserLoginAgent;
import com.ftpserver.event.Event;
import com.ftpserver.event.EventHandler;
import com.ftpserver.exceptions.FileIsDirectoryException;
import com.ftpserver.exceptions.FileIsNotDirectoryException;
import com.ftpserver.fileIO.FileIO;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;

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

    private Socket dataSocket = null;

    private String currentPath = "/";

    private FileIO fileIOInstance = FileIO.getInstance();

    private EventHandler onResponseEventHandler;

    public CMDHandler(Object obj, String mtd) {
        onResponseEventHandler = new EventHandler();
        onResponseEventHandler.addEvent(new Event(obj, mtd));
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
        isloggedin = UserLoginAgent.userAuthenticate(username, userpass);
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

        try {
            String temp = fileIOInstance.appendFilePath(currentPath, args);
            temp = fileIOInstance.cddir(temp);
            response(Statics.CWD_SUCC_RETURN);
            currentPath = temp;
        } catch (Exception e) {
            response(Statics.CWD_FAILED_RETURN);
            throw e;
        }
    }

    public void CDUP(String args) throws Exception {
        try {
            if (currentPath.equals("/")) {
                response(Statics.CDUP_SUCC_RETURN);
                return;
            }

            currentPath = fileIOInstance.appendFilePath(currentPath, "../");
            currentPath = fileIOInstance.cddir(currentPath);
            response(Statics.CDUP_SUCC_RETURN);

        } catch (Exception e) {
            response(Statics.CDUP_FAILED_RETURN);
            throw e;
        }
    }

    public void PWD(String args) throws Exception {
        response(Statics.PWD_RETURN + "\"/" + currentPath + "\"\n");
    }

    public void SYST(String args) throws Exception {
        response(Statics.SYST_RETURN);
    }

    public void FEAT(String args) throws Exception {
        response(Statics.FEAT_RETURN);
    }

    public void OPTS(String args) throws Exception {
        response(Statics.OPTS_RETURN);
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
        pasvSocket.setSoTimeout(0);
        if (netMode == Statics.TRANSFER_MODE.PORT) {
            dataSocket = new Socket(this.portHost, this.portPort);
        } else {
            if (dataSocket == null) {
                dataSocket = pasvSocket.accept();
            }
        }
    }

    public void PASV(String args) throws Exception {
        try {
            if (pasvSocket != null) {
                pasvSocket.close();
            }
            int p = getUnusedPort();
            pasvSocket = new ServerSocket(p);
            String ip = InetAddress.getLocalHost().getHostAddress().replace('.', ',');
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
//        List<Byte> blist = new ArrayList<>();
//        for (int i = 0;i < buffer.length;i++) {
//            blist.add(buffer[i]);
//        }
//        byte r = '\r';
//        blist.remove(new Byte(r));
//        byte b[] = new byte[blist.size()];
//        for (int i = 0;i < b.length;i++) {
//            b[i] = blist.get(i);
//        }
//        return b;
        byte ret[] = new byte[originalLength];
        int k = 0;
        for (int i = 0; i < originalLength; i++) {
            if (buffer[i] != '\r') {
                ret[k] = buffer[i];
                k++;
            }
        }
//        byte ret[] = new byte[originalLength];
//        for (int i = 0;i < originalLength;i++) {
//            ret[i] = buffer[i];
//        }
//        return ret;
        buffer = new byte[k];
        for (int i = 0; i < k; i++) {
            buffer[i] = ret[i];
        }
        return buffer;
    }

    public void RETR(String args) throws Exception {
        try {
            String filepath = args.trim();
            setDataSocket();
            FileInputStream filereader = fileIOInstance.open(filepath);
            byte buffer[] = new byte[Statics.FILE_READ_BUFFER_LENGTH];
            OutputStream ostream = dataSocket.getOutputStream();
            int current_length = 0;
            if (netType == Statics.TRANSFER_TYPE.ASCII) {
                response(Statics.RETR_STRART_A_RETURN);
            } else {
                response(Statics.RETR_STRART_I_RETURN);
            }

            while ((current_length = filereader.read(buffer)) != -1) {
                if (netType == Statics.TRANSFER_TYPE.ASCII) {
                    buffer = parseReturnChar(buffer, current_length);
//                    int minus = Statics.FILE_READ_BUFFER_LENGTH - buffer.length;
                    current_length = buffer.length;

                }
                ostream.write(buffer, 0, current_length);
                buffer = new byte[Statics.FILE_READ_BUFFER_LENGTH];
            }
            ostream.close();
            filereader.close();
            //dataSocket.close();
            response(Statics.RETR_SUCC_RETURN);
        } catch (Exception e) {
            response(Statics.RETR_FAILED_RETURN);
            throw e;
        }
    }

    public void STOR(String args) throws Exception {
        args = args.trim();
        try {
            setDataSocket();
            InputStream istream = dataSocket.getInputStream();
            byte buffer[] = new byte[Statics.NET_READ_BUFFER_LENGTH];
            int amount = 0;
            if (netType == Statics.TRANSFER_TYPE.ASCII) {
                response(Statics.STOR_STRART_A_RETURN);
            } else {
                response(Statics.STOR_STRART_I_RETURN);
            }
            while ((amount = istream.read(buffer)) != -1) {

                fileIOInstance.write(fileIOInstance.appendFilePath(currentPath, args), buffer, amount);
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
        try {
            String temp = fileIOInstance.appendFilePath(currentPath, args);
            fileIOInstance.mkDir(temp);
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
        try {
            String temp = fileIOInstance.appendFilePath(currentPath, args);
            fileIOInstance.rmdir(temp);
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
        try {
            String temp = fileIOInstance.appendFilePath(currentPath, args);
            fileIOInstance.rmfile(temp);
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
        if (dataSocket.isConnected()) {
            dataSocket.close();
            response(Statics.ABOR_SUCC_RETURN);
        } else {
            response(Statics.ABOR_FAILED_RETURN);
        }
    }

    public void LIST(String args) throws Exception {
        try {
            response(Statics.LIST_START_RETURN);
            setDataSocket();
            OutputStream ostream = dataSocket.getOutputStream();
//            for (String i : files) {
//                String type = "- ";
//                if (fileIOInstance.isDir(i)) {
//                    type = "d ";
//                }
//                ostream.write(type.getBytes());
//            }
            String str = fileIOInstance.lsdir(currentPath);
            ostream.write(str.getBytes());
            response(Statics.LIST_SUCC_RETURN);
            dataSocket.close();
        } catch (Exception e) {
            response(Statics.LIST_FAILED_RETURN);
            throw e;
        }
    }

}
