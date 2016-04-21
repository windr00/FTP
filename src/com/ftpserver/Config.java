package com.ftpserver;

/**
 * Created by windr on 4/19/16.
 */
public class Config {

    private static Config _instance;
    private String root = "/Users/windr/";

    private String lsCMD = "/bin/ls -l ";
    private String cdCMD = "/usr/bin/cd ";
    private String pwdCMD = "/bin/pwd";

    public static Config getInstance() {
        if (_instance == null) {
            _instance = new Config();
        }
        return _instance;
    }

    public String getCdCMD() {
        return cdCMD;
    }

    public void setCdCMD(String cdCMD) {
        this.cdCMD = cdCMD;
    }

    public String getPwdCMD() {
        return pwdCMD;
    }

    public void setPwdCMD(String pwdCMD) {
        this.pwdCMD = pwdCMD;
    }

    public String getLsCMD() {
        return lsCMD;
    }

    public void setLsCMD(String lsCMD) {
        this.lsCMD = lsCMD;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}
