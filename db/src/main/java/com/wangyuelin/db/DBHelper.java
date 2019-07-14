
package com.wangyuelin.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SQLiteOpenHelper可以使用单例
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db"; //数据库名称
    private static final int DB_VERSION = 1; //数据库版本
    static final Lock lock = new ReentrantLock(); //锁
    private List<TableEntity> tables; //需要创建的表
    private DBListener dbListener;

    private DBHelper(Context context) {
        //这里只是保存了创建数据库的信息，真正的创建表（如onCreate调用），是在 getWritableDatabase/getReadableDatabase 第一次 调用的时候
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 在这里创建表
     * 只会调用一次
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        if (tables != null) {
            for (TableEntity table : tables) {
                db.execSQL(table.buildTableString());
            }
        }
    }


    /**
     * 升级
     * 可调用多次
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //将升级逻辑得动作暴露出去
        if (dbListener != null) {
            dbListener.onUpgrade(db, oldVersion, newVersion);
        }

    }

    /**
     * 降级
     * 可调用多次
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     *
     * 当用户调用getWritableDatabase或是getReadableDatabase时。
     * 1. 如果数据库不存在，就会调用onCreat(),不会调用onUpgrade();
     * 2. 如果数据库存在，但是版本不一样就调用onUpgrade(),不会调用onCreate();
     * 3. 如果数据库存在，版本一样,不会调用onCreate(), onUpgrade();
     * 4. 当调用getWritableDatabase，getReadableDatabase如果数据库没有打开，就调用onOpen方法，如果打开了就不调onOPen；
     * 5. 数据库的表的创建一般都在SQLiteOpenHelper的onCreat()中，表字段升级，都会在onUpgrade()处理；
     *
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //将降级逻辑得动作暴露出去
        if (dbHelper != null) {
            dbHelper.onDowngrade(db, oldVersion, newVersion);
        }
    }

    private static DBHelper dbHelper;

    //单例获取SQLiteOpenHelper
    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            synchronized (DBHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new DBHelper(context);
                }
            }
        }
        return dbHelper;
    }

    /**
     * 将需要创建的表传递进来
     * @param tables
     */
    public void setCreateTables(List<TableEntity> tables) {
        this.tables = tables;
    }

    /**
     * 设置数据库的回调
     * @param dbListener
     */
    public void setDbListener(DBListener dbListener) {
        this.dbListener = dbListener;
    }

    public interface DBListener{
        void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);
        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }

}
