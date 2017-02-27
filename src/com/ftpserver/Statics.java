package com.ftpserver;

/**
 * Created by windr on 4/19/16.
 */
public class Statics {

    public static final int FILE_READ_BUFFER_LENGTH = 1024;

    public static final int NET_READ_BUFFER_LENGTH = 65536;
    public static final String COMMAND_NOT_UNDERSTOOD_RETURN = "500 COMMAND NOT UNDERSTOOD\n";
    public static final String INIT_RETURN = "220 SERVICE READY\n";
    public static final String AUTH_RETURN = "500 LTS NOT SUPPORTED\n";
    public static final String FEAT_RETURN = "211-FEATURES SUPPORTED\nUTF8\n211 END\n";
    public static final String OPTS_UTF8_ON_RETURN = "200 OPTS UTF8 IS SET TO ON.\n";
    public static final String OPTS_UTF8_OFF_RETURN = "200 OPTS UTF8 IS SET TO OFF.\n";
    public static final String USER_RETURN = "331 NEED PASSWORD.\n";
    public static final String PASS_LOGEDIN_RETURN = "230 WELCOME !\n";
    public static final String PASS_FAILED_RETURN = "530 LOGIN OR PASSWORD INCORRECT\n";
    public static final String QUIT_RETURN = "221 GOOD BYE!\n";
    public static final String TYPE_RETURN = "200 TYPE SET TO ";
    public static final String TYPE_FAILED_RETURN = "501 WRONG TYPE\n";
    public static final String NOOP_RETURN = "200 NOOP OK.\n";
    public static final String CWD_SUCC_RETURN = "250 CWD SUCCESSFUL.\n";
    public static final String CWD_FAILED_RETURN = "550 CWD FAILED\n";
    public static final String PWD_RETURN = "257 CURRENT DIRECTORY IS ";
    public static final String PORT_FAILED_RETURN = "500 PORT PARAM ERROR.\n";
    public static final String PORT_SUCC_RETURN = "200 PORT SUCCESSFUL\n";
    public static final String PASV_SUCC_RETURN = "227 ENTERING PASSIVE MODE (";
    public static final String PASV_FAILED_RETURN = "500 ERROR ENTERING PASSIVE MODE \n";
    public static final String RETR_STRART_A_RETURN = "150 OPENING ASCII MODE DATA CONNECTION.\n";
    public static final String RETR_STRART_I_RETURN = "150 OPENING BINARY MODE DATA CONNECTION.\n";
    public static final String RETR_SUCC_RETURN = "226 TRANSFER COMPLETE\n";
    public static final String RETR_FAILED_RETURN = "550 FILE NOT FOUND OR ACCESS DENIED.\n";
    public static final String STOR_SUCC_RETURN = "226 TRANSFER COMPLETE\n";
    public static final String STOR_FAILED_RETURN = "550 FILE ALREADY EXISTS OR ACCESS DENIED.\n";
    public static final String STOR_STRART_A_RETURN = "150 OPENING ASCII MODE DATA CONNECTION.\n";
    public static final String STOR_STRART_I_RETURN = "150 OPENING BINARY MODE DATA CONNECTION.\n";
    public static final String LIST_START_RETURN = "150 OPENING ASCII MODE DATA CONNECTION.\n";
    public static final String LIST_FAILED_RETURN = "500 LIST ERROR\n";
    public static final String LIST_SUCC_RETURN = "226 LIST TRANSFER COMPLETE\n";
    public static final String CDUP_SUCC_RETURN = "250 CDUP SUCCESSFUL\n";
    public static final String CDUP_FAILED_RETURN = "500 CDUP FAILED\n";
    public static final String ABOR_SUCC_RETURN = "200 TRANSFER ABORT!\n";
    public static final String ABOR_FAILED_RETURN = "500 NO TRANSFER GOING\n";
    public static final String MKD_SUCC_RETURN = "250 MKD SUCCESSFUL\n";
    public static final String MKD_FAILED_RETURN = "550 MKD FALIED\n";
    public static final String MKD_EXIST_WARN_RETURN = "500 MKD FALIED ALREADY EXISTS\n";
    public static final String DELE_SUCC_RETURN = "250 DELETE SUCCESSFUL\n";
    public static final String DELE_FALIED_RETURN = "550 DELETE FALIED\n";
    public static final String DELE_ISDIR_WARN_RETURN = "550 DELETE FALIED FILE IS A DIRECTORY\n";
    public static final String RMD_SUCC_RETURN = "250 RM DIRECTORY SUCCESSFUL\n";
    public static final String RMD_FALIED_RETURN = "250 RM DIRECTORY FALIED\n";
    public static final String RMD_ISFILE_WARN_RETURN = "550 RM DIRECTORY FALIED FILE IS NOT A DICRECTORY\n";
    public static final String RNFR_SUCC_RETURN = "350 RNFR SUCCESSFUL\n";
    public static final String RNFR_FALIED_RETURN = "550 RNFR FAILED\n";
    public static final String RNTO_SUCC_RETURN = "250 RNTO SUCCESSFUL\n";
    public static final String RNTO_FAILED_RETURN = "550 RNTO FAILED\n";
    public static final String SIZE_SUCC_RETURN = "213 ";
    public static final String SIZE_FAILED_RETURN = "500 GET SIZE FAILED\n";
    public static final String CMD_NOT_ALLOWED_RETURN = "450 CMD NOT ALLOWED, YOU MAY NEED TO LOGIN FIRST\n";
    public static String SYSTEM_STASH;
    public static String CURRENT_JAR_PATH;
    public static String SYST_RETURN = "215 ";

    static {
        String os = System.getProperty("os.name").toUpperCase();
        if (os.startsWith("WIN")) {
            SYSTEM_STASH = "\\";
        } else {
            SYSTEM_STASH = "/";
        }
        SYST_RETURN = SYST_RETURN + os + "\n";


        java.net.URL url = Statics.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }
        java.io.File file = new java.io.File(filePath);
        CURRENT_JAR_PATH = file.getAbsolutePath();

    }


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
