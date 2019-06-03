package com.wangyuelin.db.demo;

/**
 * 示例如何使用
 */
public class UseUser {
    private UserDao userDao = new UserDao();

    /**
     * 据id获取用户
     * @param id
     * @return
     */
    public User get(int id) {
        return userDao.queryOne(User.ID  + "=?", new String[]{String.valueOf(id)});
    }
}
