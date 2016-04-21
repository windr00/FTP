package com.ftpserver.exceptions;

/**
 * Created by Micro on 2016/4/21.
 */
public class FileIsNotDirectoryException extends Exception {
    private String filepath;

    public FileIsNotDirectoryException(String filepath) {
        this.filepath = filepath;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
