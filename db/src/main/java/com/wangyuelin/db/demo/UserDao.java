package com.wangyuelin.db.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.wangyuelin.common.BaseApplication;
import com.wangyuelin.db.BaseDao;
import com.wangyuelin.db.ColumnEntity;
import com.wangyuelin.db.DBHelper;
import com.wangyuelin.db.TableEntity;

public class UserDao extends BaseDao<User> {

    /**
     * 创建实例，打开读写的数据库
     */
    public UserDao() {
        super(DBHelper.getInstance(BaseApplication.getApplication()));
    }

    @Override
    public TableEntity getTable() {
        TableEntity userTable = new TableEntity(getTableName());
        userTable.addColumn(new ColumnEntity(User.ID, "INTEGER", true, true, true))
                .addColumn(new ColumnEntity(User.AGE, "INTEGER"))
                .addColumn(new ColumnEntity(User.AGE, "VARCHAR"));
        return userTable;
    }

    @Override
    protected String getTableName() {
        return "user";
    }


    @Override
    public void unInit() {

    }

    @Override
    public User parseCursorToBean(Cursor cursor) {
        return User.parseCursorToBean(cursor);
    }

    @Override
    public ContentValues getContentValues(User user) {
        return User.buildContentValues(user);
    }


}
