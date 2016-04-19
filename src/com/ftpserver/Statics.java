package com.ftpserver;

/**
 * Created by windr on 4/19/16.
 */
public class Statics {
    public static final String USER_RETURN = "331 NEED PASSWORD.";
    public static final String PASS_LOGEDIN_RETURN = "230 WELCOME !";

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

    public enum NET_TRANSFER_TYPE {
        UPLOAD,
        DOWNLOAD
    }

    public enum TRANSFER_TYPE {
        ASCII,
        BINARY
    }

}
