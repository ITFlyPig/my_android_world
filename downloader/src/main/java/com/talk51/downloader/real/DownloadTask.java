package com.talk51.downloader.real;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.SparseArray;

import okhttp3.Call;

public class DownloadTask  {
    public int id;
    private volatile SparseArray<Object> tags;
    private String url;
    private String saveDir;           //文件保存目录
    private String fileName;          //文件保存名称
    private String showFileName;      //文件的显示名称
    public int state;                //任务的状态
    public long totalSize;     //资源的大小
    public long currentSize;   //当前下载数量
    public long date;         //创建时间
    public long completeTime;  //下载完成的时间
    public String fileType;     //文件的类型
    public String extra;        //存储扩展数据
    public String belongUid;    //标志任务属于的用户

    public OnCancelListener onCancelListener;//取消时的回调


    public volatile boolean stop;//标志是否停止
    public Call call;//okhttp请求

//
    public DownloadTask(String url, String saveDir, String fileName, String showFileName, String fileType, String extra) {
        this.url = url;
        this.saveDir = saveDir;
        this.fileName = fileName;
        this.showFileName = showFileName;
        this.fileType = fileType;
        this.extra = extra;
    }





    public static final String TAG = "tag";
    public static final String URL = "url";
    public static final String TOTAL_SIZE = "totalSize";
    public static final String CURRENT_SZIE = "current_size";
    public static final String SAVE_DIR = "save_dir";
    public static final String STATE = "state";
    public static final String FILE_NAME = "fileName";
    public static final String SHOW_FILE_NAME = "showFileName";
    public static final String DATE = "date";
    public static final String FILE_TYPE = "file_type";
    public static final String EXTRA = "extra";
    public static final String COMPLETE_TIME = "completeTime";
    public static final String ID = "id";
    public static final String UID = "uid";





    public static class Builder{//这种方式构造的参数是不能改变的
        private String url;
        private String saveDir;           //文件保存目录
        private String fileName;          //文件保存名称
        private String showFileName;      //文件的显示名称
        private String fileType;     //文件的类型
        private String extra;        //存储扩展数据


        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setSaveDir(String saveDir) {
            this.saveDir = saveDir;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setShowFileName(String showFileName) {
            this.showFileName = showFileName;
            return this;
        }

        public Builder setFileType(String fileType) {
            this.fileType = fileType;
            return this;
        }

        public Builder setExtra(String extra) {
            this.extra = extra;
            return this;
        }

        public DownloadTask build() {
            return new DownloadTask(url, saveDir, fileName, showFileName, fileType, extra);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public String getFileName() {
        return fileName;
    }

    public String getShowFileName() {
        return showFileName;
    }



    /**
     * 定义Task对应的回调
     */
    public interface OnCancelListener {
        void onCancel(DownloadTask task);
    }


    public static DownloadTask parseCursorToBean(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        String url = cursor.getString(cursor.getColumnIndex(DownloadTask.URL));
        String saveDir = cursor.getString(cursor.getColumnIndex(DownloadTask.SAVE_DIR));
        String fileName = cursor.getString(cursor.getColumnIndex(DownloadTask.FILE_NAME));
        String showFileName = cursor.getString(cursor.getColumnIndex(DownloadTask.SHOW_FILE_NAME));
        String fileType = cursor.getString(cursor.getColumnIndex(DownloadTask.FILE_TYPE));
        String extra = cursor.getString(cursor.getColumnIndex(DownloadTask.EXTRA));
        DownloadTask task = new DownloadTask(url, saveDir, fileName, showFileName, fileType, extra);
        task.currentSize = cursor.getLong(cursor.getColumnIndex(DownloadTask.CURRENT_SZIE));
        task.state = cursor.getInt(cursor.getColumnIndex(DownloadTask.STATE));
        task.totalSize = cursor.getLong(cursor.getColumnIndex(DownloadTask.TOTAL_SIZE));
        task.date = cursor.getLong(cursor.getColumnIndex(DownloadTask.DATE));
        task.completeTime = cursor.getLong(cursor.getColumnIndex(DownloadTask.COMPLETE_TIME));
        task.id = cursor.getInt(cursor.getColumnIndex(DownloadTask.ID));
        task.belongUid = cursor.getString(cursor.getColumnIndex(DownloadTask.UID));
        return task;

    }

    public static ContentValues buildContentValues(DownloadTask task) {
        if (task == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(DownloadTask.CURRENT_SZIE, task.currentSize);
        values.put(DownloadTask.FILE_NAME, task.fileName);
        values.put(DownloadTask.SAVE_DIR, task.saveDir);
        values.put(DownloadTask.SHOW_FILE_NAME, task.showFileName);
        values.put(DownloadTask.STATE, task.state);
        values.put(DownloadTask.TOTAL_SIZE, task.totalSize);
        values.put(DownloadTask.URL, task.url);
        values.put(DownloadTask.DATE, task.date);
        values.put(DownloadTask.FILE_TYPE, task.fileType);
        values.put(DownloadTask.EXTRA, task.extra);
        values.put(DownloadTask.COMPLETE_TIME, task.completeTime);
        values.put(DownloadTask.UID, task.belongUid);
        return values;
    }

    /**
     * 设置Tag
     * @param key
     * @param value
     */
    public void putTag(int key, Object value) {
        if (tags == null) {
            tags = new SparseArray<>();
        }
        tags.put(key, value);
    }


    /**
     * 获取Tag
     * @param key
     * @return
     */
    public Object getTag(int key) {
        if (tags == null) {
            tags = new SparseArray<>();
        }
        return tags.get(key);

    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof DownloadTask) {
            final DownloadTask task = (DownloadTask) obj;
            if (TextUtils.equals(this.url, task.url)) return true;//通过url判断task是否相同
        }
        return false;
    }
}
