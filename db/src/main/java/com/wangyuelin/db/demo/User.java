package com.wangyuelin.db.demo;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;

public class User implements Serializable {


    public int id;
    public int age;
    public String name;

    public static final String ID = "id";
    public static final String AGE = "age";
    public static final String NAME = "name";

    /**
     * 将bean转为数据库操作的数据
     * @param user
     * @return
     */
    public static ContentValues buildContentValues(User user) {
        if (user == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(User.ID, user.id);
        values.put(User.AGE, user.age);
        values.put(User.NAME, user.name);
        return values;
    }

    /**
     * 将查询的结果转为bean
     * @param cursor
     * @return
     */
    public static User parseCursorToBean(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        String name = cursor.getString(cursor.getColumnIndex(User.NAME));
        int id = cursor.getInt(cursor.getColumnIndex(User.ID));
        int age = cursor.getInt(cursor.getColumnIndex(User.AGE));
        User user = new User();
        user.id = id;
        user.age = age;
        user.name = name;
        return user;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("id:").append(id).append("; name:").append(name).append("; age:").append(age);
        return buffer.toString();
    }
}
