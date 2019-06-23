package com.easybug.plugint;

import org.apache.http.util.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 类相关工具
 */
public class ClassUtil {

    /**
     * 获取类的名称
     *
     * @param fullName
     * @return
     */
    public static String getName(String fullName) {
        return "";
    }

    /**
     * 获取类的包
     *
     * @param fullName
     * @return
     */
    public static String getPackage(String fullName) {
        return "";
    }

    /**
     * 将字节数组写到class文件
     * @param bytes
     * @param filePath
     */
    public static void saveToFile(byte[] bytes, String filePath) {
        if (bytes == null || bytes.length == 0 || TextUtils.isEmpty(filePath)) {
            return;
        }

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
