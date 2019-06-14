package com.wangyuelin.performance;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 线程池帮助类，管理线程，避免一个工程中有多个线程池
 */
public class ThreadHelper {

    private static class Holder {
        private static ThreadHelper threadHelper = new ThreadHelper();
    }

    public static ThreadHelper getInstance() {
        return Holder.threadHelper;
    }

    private ThreadHelper() {
    }

    ;

    private ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * 提交任务
     * @param task
     */
    public void  submit(Runnable task) {
        executor.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return executor.submit(task, result);
    }

}
