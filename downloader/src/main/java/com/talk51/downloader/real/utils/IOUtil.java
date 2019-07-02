package com.talk51.downloader.real.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil {

    /**
     * 关闭资源
     * @param closeables
     */
    public static void closeAll(Closeable... closeables){
        if(closeables == null){
            return;
        }
        for (Closeable closeable : closeables) {
            if(closeable!=null){
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
