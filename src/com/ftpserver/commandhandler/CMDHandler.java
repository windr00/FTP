package com.ftpserver.commandhandler;

import com.ftpserver.Statics;
import com.ftpserver.agent.UserLoginAgent;
import com.ftpserver.fileIO.FileIO;

/**
 * Created by windr on 4/19/16.
 */
public class CMDHandler {

    private String username = "";

    private boolean isloggedin = false;

    private Statics.TRANSFER_TYPE netType;

    private String currentPath = "/";

    private FileIO fileIOInstance = FileIO.getInstance();

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
}
