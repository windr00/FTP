package com.ftpserver.fileIO;

import com.ftpserver.Config;
import com.ftpserver.exceptions.FileIsDirectoryException;
import com.ftpserver.exceptions.FileIsNotDirectoryException;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

/**
 * Created by windr on 4/19/16.
 */
public class FileIO {

    private static FileIO _instance;

    public static FileIO getInstance() {
        if (_instance == null) {
            _instance = new FileIO();
        }
        return _instance;
    }

    public void rmfile(String path) throws Exception {
        File file = new File(path);
        if (file.isDirectory()) {
            throw new FileIsDirectoryException(path);
        }
        if (!file.delete()) {
            throw new FileNotFoundException(path);
        }
    }

    public void rmdir(String path) throws Exception {
        File file = new File(path);
        if (!file.isDirectory()) {
            throw new FileIsNotDirectoryException(path);
        }
        rcrmfile(file);
        if (!file.delete()) {
            throw new FileNotFoundException(path);
        }
    }

    private void rcrmfile(File file) throws Exception {
        File[] list = file.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                if (f.listFiles().length == 0) {
                    f.delete();
                } else {
                    rcrmfile(f);
                }
            } else {
                f.delete();
            }
        }
    }

    public byte[] read(String path) throws Exception {
        File file = new File(Config.getInstance().getRoot() + path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String tmp = null;
        StringBuilder sb = new StringBuilder();

        tmp = br.readLine();
        while (tmp != null) {
            sb.append(tmp);
            tmp = br.readLine();
        }
        br.close();

        return sb.toString().getBytes();
    }

    public void create(String path) throws Exception {
        File file = new File(path);
        if (file.exists()) {
            throw new FileAlreadyExistsException(path);
        }
        if (!file.createNewFile()) {
            throw new FileNotFoundException(path);
        }
    }

    public void write(String path, byte[] content, int length) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException(path);
        }
        FileOutputStream fos = new FileOutputStream(file, true);
        byte buffer[] = new byte[length];
        for (int i = 0; i < length; i++) {
            buffer[i] = content[i];
        }
        fos.write(buffer);
        //fos.write(content, 0, length);
        fos.close();
    }

    public void mkDir(String path) throws Exception {

        File file = new File(path);
        if (file.exists()) {
            throw new FileAlreadyExistsException(path);
        }
        if (!file.mkdir()) {
            throw new NoSuchFileException(path);
        }


    }

    public boolean isDir(String path) throws Exception {
        return new File(path).isDirectory();
    }

    public boolean exist(String path) {
//        String uri = URI.create(Config.getInstance().getRoot()).toASCIIString();
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }

    public FileInputStream open(String path) throws Exception {
        File file = new File(path);
        return new FileInputStream(file);
    }

    public String cddir(String path) throws Exception {
//        Process p = Runtime.getRuntime().exec(Config.getInstance().getCdCMD() + Config.getInstance().getRoot() + path + ";" + Config.getInstance().getPwdCMD());
//        InputStream istream = p.getInputStream();
//        BufferedReader br = new BufferedReader(new InputStreamReader(istream));
//        String result = br.readLine();
//
//        if (br.readLine() != null){
//            br.close();
//            istream.close();
//            throw new NoSuchFileException(path);
//        }
//        istream.close();
//        br.close();
//        p.destroy();
//        result = appendStash(result);
//        if (result.startsWith(Config.getInstance().getRoot())) {
//            return result.substring(Config.getInstance().getRoot().length());
//        }
//        else {
//            throw new NoSuchFileException(path);
//        }
        // path = appendFilePath(Config.getInstance().getRoot(), path);

        String pathcuts[] = path.split("/");
        for (int i = 0; i < pathcuts.length; i++) {
            if (pathcuts[i].equals("..")) {
                pathcuts[i] = "";
                for (int j = i - 1; j > 0; j++) {
                    if (!pathcuts[j].equals("")) {
                        pathcuts[j] = "";
                        break;
                    }
                }
            }
        }
        String result = "/";
        for (int i = 0; i < pathcuts.length; i++) {
            if (!pathcuts[i].equals("")) {
                result += pathcuts[i] + "/";
            }
        }
        if (!result.startsWith(appendStash(Config.getInstance().getRoot()))) {
            throw new FileNotFoundException(path);
        } else {
            return appendStash(result.substring(Config.getInstance().getRoot().length()));
        }
    }

    public String lsdir(String path) throws Exception {
        String ret = "";
//        path = appendFilePath(Config.getInstance().getRoot(), path);
        Process p = Runtime.getRuntime().exec(Config.getInstance().getLsCMD() + path);
        System.out.println(path);
        InputStream istream = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(istream));
        String result = "";
        while ((result = br.readLine()) != null) {
            ret += result + "\n";
        }
        br.close();
        istream.close();
        return ret;
    }

    public String appendFilePath(String currentpath, String newPath) {
        if (!currentpath.endsWith("/")) {
            currentpath = currentpath.concat("/");
        }
        if (!newPath.endsWith("/")) {
            newPath = newPath.concat("/");
        }
        if (newPath.startsWith("/")) {
            newPath = newPath.substring(1);
        }
        return currentpath.concat(newPath);
    }

    public String appendStash(String path) {
        if (!path.endsWith("/")) {
            path = path.concat("/");
        }
        return path;
    }
}
