package com.honeywell.cube.common.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

/**
 * Created by H157925 on 16/4/13. 10:45
 * Email:Shodong.Sun@honeywell.com
 */
public class IOUtils {
    public static void closeInputStream(InputStream input) {
        if(null == input) {
            return;
        }
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeFileInputStream(FileInputStream input) {
        if(null == input) {
            return;
        }
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeFileOutputStream(FileOutputStream out) {
        if(null == out) {
            return;
        }
        try {
            out.close();
            out = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeOutputStream(OutputStream out) {
        if(null == out) {
            return;
        }
        try {
            out.close();
            out = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeInputStream(InputStreamReader inReader) {
        if(null == inReader) {
            return;
        }
        try {
            inReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeOutputStream(OutputStreamWriter outWriter) {
        if(null == outWriter) {
            return;
        }
        try {
            outWriter.close();
            outWriter = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeRAStream(RandomAccessFile stream) {
        if(null == stream) {
            return;
        }
        try {
            stream.close();
            stream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
