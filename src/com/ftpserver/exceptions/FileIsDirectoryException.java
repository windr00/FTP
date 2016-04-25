package com.ftpserver.exceptions;

/**
 * Created by Micro on 2016/4/21.
 */
public class FileIsDirectoryException extends Exception {
    private String filename = "";

    public FileIsDirectoryException(String filename) {
        this.filename = filename;
    }
}
