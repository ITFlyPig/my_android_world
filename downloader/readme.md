
使用步骤：

#### 1、初始化
private void initDownloader() {
        String saveDir = getActivity().getFilesDir().getAbsolutePath() + File.separator + GlobalParams.user_id;
        DownloadConf conf = new DownloadConf.Builder()
                .setMaxParalle(5)
                .setmSaveDir(saveDir)
                .setUid(GlobalParams.user_id)
                .build();
        RealDownloader.getInstace().setConfig(conf);
        Talk51DownloadManager.getInstance().updateDB(getContext(), GlobalParams.user_id);
    }
    
#### 2、开始下载

RealDownloader.getInstace().start(下载链接, 下载任务的名称, 下载的文件类型, 这里可以传递额外的数据;