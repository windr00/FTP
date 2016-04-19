package com.ftpserver;

/**
 * Created by windr on 4/19/16.
 */
public class Statics {
    public static final String USER_RETURN = "331 NEED PASSWORD.";
    public static final String PASS_LOGEDIN_RETURN = "230 WELCOME !";

    public static final int FILE_READ_BUFFER_LENGTH = 1024;

    //    λ	接入命令：USER、PASS、ACCT、REIN、QUIT和ABOR；
//    λ	文件管理命令：CWD、CDUP、DELE、LIST、NLIST、MKD、PWD、RMD、RNFR、RNTO和SMNT；
//    λ	数据格式化命令：TYPE、STRU、MODE；
//    λ	端口定义命令包括PORT和PASV;
//    λ	文件传送命令：RETR、STOR、APPE、STOU、ALLO、REST和STAT；
//    λ	杂项命令：HELP、NOOP、SITE和SYST
    public static final String PASS_FAILED_RETURN = "";
    public static final String QUIT_RETURN = "221 GOOD BYE!";
    public static final String TYPE_RETURN = "200 TYPE SET TO ";
    public static final String TYPE_FAILED_RETURN = "";
    public static final String NOOP_RETURN = "200 NOOP OK.";
    public static final String CWD_SUCC_RETURN = "250 CWD SUCCESSFUL.";
    public static final String CWD_FAILED_RETURN = "";
    public static final String PWD_RETURN = "257 ";
    public static final String PORT_FAILED_RETURN = "500 PORT PARAM ERROR.";
    public static final String PORT_SUCC_RETURN = "200 PORT SUCCESSFUL";
    public static final String RETR_STRART_RETURN = "150 OPENING ASCII MODE DATA CONNECTION.";
    public static final String RETR_SUCC_RETURN = "226 TRANSFER COMPLETE";
    public static final String RETR_FAILED_RETURN = "550 FILE NOT FOUND OR ACCESS DENIED.";


    public enum NET_TRANSFER_TYPE {
        UPLOAD,
        DOWNLOAD
    }

    public enum TRANSFER_TYPE {
        ASCII,
        BINARY
    }

    public enum TRANSFER_MODE {
        PORT,
        PASV
    }

}
