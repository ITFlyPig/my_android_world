package com.wangyuelin.myandroidworld.util;

import android.text.TextUtils;


import com.wangyuelin.common.BaseApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * 我只想简单的保存几个对象，SP不满足，DB又太重量级和复杂一点。
 * ObjectCacheUtil解决的需求是介于SP和DB之间
 *
 * 效率对比
 * 方案：序列化对象，然后存在SP，效率没有直接写文件高
 *
 * 实际测试结果：直接写文件比commit写sp效率高
 */
public class ObjectCache {

    public static ObjectCache getInstance() {
        return Holder.objectCacheUtil;
    }

    private static class Holder {
        private static ObjectCache objectCacheUtil = new ObjectCache();
    }

    private ObjectCache() {
        //从磁盘读取
        cacheMap = new HashMap<>();
//        ThreadHelper.getInstance().submit(() -> cacheAllObjectFromDisk());
        cacheAllObjectFromDisk();
    }

    private HashMap<String, Object> cacheMap;

    /**
     * 将磁盘的上的对象读出来缓存
     */
    private void cacheAllObjectFromDisk() {
        String dir = getDir();
        File file = new File(dir);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        File[] objectFiles =  file.listFiles();
        if (objectFiles == null) {
            return;
        }
        for (File objFile : objectFiles) {
            String name = objFile.getName();
            Object obj = getObejct(objFile);
            if (obj != null) {
                cacheMap.put(name, obj);
            }
        }

    }


    /**
     * 获得缓存的路径
     *
     * @return
     */
    private String getDir() {
        String dir = BaseApplication.getApplication().getFilesDir() + File.separator + "object_cache";
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        return dir;
    }



    /**
     * 据名称获取对象
     * @param name
     * @param <T>
     * @return
     */
    public synchronized  <T> T getObejct(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        Object o = cacheMap.get(name);
        if (o == null) {
            return null;
        }

        T t = null;
        try {
            t = (T) o;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 获取对象
     * @param f
     * @param <T>
     * @return
     */
    private  <T> T getObejct(File f) {
        if (f == null) {
            return null;
        }
        File file = f;
        if (!file.exists()) {
            return null;
        }

        Object o = getObject(file);
        if (o == null) {
            return null;
        }

        T t = null;
        try {
            t = (T) o;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 从文件读取对象
     * @param file
     * @return
     */
    private Object getObject(File file) {
        if (file == null) {
            return null;
        }
        ObjectInputStream objectInputStream = null;
        try {
             objectInputStream = new ObjectInputStream(new FileInputStream(file));
            return objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 同步保存对象
     * @param name
     * @param obj
     */
    public synchronized void save(final String name, final Object obj) {
        if (!check(obj)) {
            return;
        }
        cacheMap.put(name, obj);
        saveToFile(name, obj);
    }

    /**
     * 校验参数
     * @param obj
     * @return
     */
    private boolean check(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Serializable)) {
            throw new IllegalArgumentException("要持久化的对象必须可序列化");
        }
        return true;

    }

    /**
     * 异步保存对象
     * @param name
     * @param obj
     */
    public synchronized void saveAsync(final String name, final Object obj) {
        if (!check(obj)) {
            return;
        }
        cacheMap.put(name, obj);

        //异步保存
        ThreadHelper.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                saveToFile(name, obj);
            }
        });

    }

    /**
     * 据名称获得文件
     * @param name
     * @return
     */
    private File getFile(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        File file = new File(getDir() + File.separator + name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;

    }

    /**
     * 对象保存到文件
     * @param name
     * @param obj
     */
    private void saveToFile(String name, Object obj) {
       File targetFile = getFile(name);
       if (targetFile == null) {
           return;
       }

        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(targetFile));
            objectOutputStream.writeObject(obj);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
