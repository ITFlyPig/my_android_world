package com.talk51.downloader.real;

/**
 * 下载参数的配置
 */
public class DownloadConf {
    public String mSaveDir; //文件保存路径
    public String uid = "def_user";      //用户的id
    public int maxParalle = 5;  //最大同时下载数

    public DownloadConf(String mSaveDir, String uid, int maxParalle) {
        this.mSaveDir = mSaveDir;
        this.uid = uid;
        if (maxParalle > 0) this.maxParalle = maxParalle;

    }

    public static class Builder{
        private String mSaveDir; //文件保存路径
        private String uid;      //用户的id
        private int maxParalle;


        public Builder setmSaveDir(String mSaveDir) {
            this.mSaveDir = mSaveDir;
            return this;
        }

        public Builder setUid(String uid) {
            this.uid = uid;
            return this;
        }

        public Builder setMaxParalle(int maxParalle) {
            this.maxParalle = maxParalle;
            return this;
        }

        public com.talk51.downloader.DownloadConf build() {
            return new com.talk51.downloader.DownloadConf(mSaveDir, uid, maxParalle);
        }

    }

}
