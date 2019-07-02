
package com.talk51.downloader.real.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.talk51.downloader.real.DownloadTask;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class DBHelper extends SQLiteOpenHelper {

    private static final String DB_CACHE_NAME = "download_file.db";
    private static final int DB_CACHE_VERSION = 1;
    static String TABLE_DOWNLOAD_PRE = "download";

    static final Lock lock = new ReentrantLock();

    private TableEntity downloadTableEntity;

    private String uid;


    DBHelper(Context context, String uid) {
        super(context, DB_CACHE_NAME, null, DB_CACHE_VERSION);
        this.uid = uid;
        downloadTableEntity = new TableEntity(DBUtils.getTableName(TABLE_DOWNLOAD_PRE, uid));
        downloadTableEntity.addColumn(new ColumnEntity(DownloadTask.ID, "INTEGER", true, true, true))//
                .addColumn(new ColumnEntity(DownloadTask.TAG, "VARCHAR"))
                .addColumn(new ColumnEntity(DownloadTask.FILE_NAME, "VARCHAR"))
                .addColumn(new ColumnEntity(DownloadTask.SHOW_FILE_NAME, "VARCHAR"))
                .addColumn(new ColumnEntity(DownloadTask.CURRENT_SZIE, "INTEGER"))
                .addColumn(new ColumnEntity(DownloadTask.TOTAL_SIZE, "INTEGER"))
                .addColumn(new ColumnEntity(DownloadTask.SAVE_DIR, "VARCHAR"))
                .addColumn(new ColumnEntity(DownloadTask.STATE, "INTEGER"))
                .addColumn(new ColumnEntity(DownloadTask.URL, "INTEGER"))
                .addColumn(new ColumnEntity(DownloadTask.DATE, "INTEGER"))
                .addColumn(new ColumnEntity(DownloadTask.FILE_TYPE, "VARCHAR"))
                .addColumn(new ColumnEntity(DownloadTask.COMPLETE_TIME, "INTEGER"))
                .addColumn(new ColumnEntity(DownloadTask.EXTRA, "VARCHAR"))
                .addColumn(new ColumnEntity(DownloadTask.UID, "VARCHAR"));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("updateDB", "创建表：" + downloadTableEntity.buildTableString());
        db.execSQL(downloadTableEntity.buildTableString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DBUtils.isNeedUpgradeTable(db, downloadTableEntity))
            db.execSQL("DROP TABLE IF EXISTS " + DBUtils.getTableName(TABLE_DOWNLOAD_PRE, uid));
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public TableEntity getDownloadTableEntity() {
        return downloadTableEntity;
    }
}
