/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.talk51.downloader.real.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.talk51.downloader.real.DownloadTask;

import java.util.List;


public class DBTaskManager extends BaseDao<DownloadTask> {
    public String uid;


    public DBTaskManager(Context context, String uid) {
        super(new DBHelper(context, uid));
        this.uid = uid;
    }

    @Override
    public DownloadTask parseCursorToBean(Cursor cursor) {
        return DownloadTask.parseCursorToBean(cursor);
    }

    @Override
    public ContentValues getContentValues(DownloadTask task) {
        return DownloadTask.buildContentValues(task);
    }

    @Override
    public String getTableName() {
        return DBUtils.getTableName(DBHelper.TABLE_DOWNLOAD_PRE, uid);
    }

    @Override
    public void unInit() {
    }


    /**
     * 获取下载任务
     */
    public DownloadTask get(String url) {
        return queryOne(DownloadTask.URL + "=?", new String[]{url});
    }

    /**
     * 移除下载任务
     */
    public void delete(String url) {
        delete(DownloadTask.URL + "=?", new String[]{url});
    }

    /**
     * 更新下载任务
     */
    public boolean update(final DownloadTask task) {
        Log.d("mm", "更新状态：" + task.state);
        update(task, DownloadTask.URL + "=?", new String[]{task.getUrl()});
        return true;//没有依赖于返回值的操作，直接返回ture
    }

    /**
     * 获取所有下载信息
     */
    public List<DownloadTask> getAll() {
        return query(null, null, null, null, null, DownloadTask.DATE + " ASC", null);
    }


    /**
     * 清空下载任务
     */
    public boolean clear() {
        return deleteAll();
    }
}
