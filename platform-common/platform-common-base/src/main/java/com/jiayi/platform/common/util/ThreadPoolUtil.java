package com.jiayi.platform.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author : weichengke
 * @date : 2019-03-04 10:45
 */
@Slf4j
public class ThreadPoolUtil {

    private static ThreadPoolUtil threadPool;
    private ThreadPoolExecutor executor;
    private int corePoolSize = 32;          // 核心池的大小 运行线程的最大值 当线程池中的线程数目达到corePoolSize后，就会把多余的任务放到缓存队列当中；
    private int maximumPoolSize = 100;  // 创建线程最大值
    private long keepAliveTime = 300;     // 线程没有执行任务时 被保留的最长时间 超过这个时间就会被销毁 直到线程数等于 corePoolSize

    /**
     * 用来储存等待中的任务的容器
     * 几种选择：
     * ArrayBlockingQueue;
     * LinkedBlockingQueue;
     * SynchronousQueue;
     */
    private LinkedBlockingQueue workQueue = new LinkedBlockingQueue<Runnable>();

    /**
     * 单例
     *
     * @return
     */
    public static ThreadPoolUtil getInstance() {
        if (threadPool == null)
            synchronized (ThreadPoolUtil.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPoolUtil();
                }
            }
        return threadPool;
    }

    public ThreadPoolUtil create() {
        return new ThreadPoolUtil();
    }

    /**
     * 私有构造方法
     */
    private ThreadPoolUtil() {
        //实现线程池
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
        log.info("getInstance thread pool success");
    }

    /**
     * 线程池获取方法
     *
     * @return
     */
    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    /**
     * 准备执行 抛入线程池
     *
     * @param t
     */
    public void execute(Thread t) {
        executor.execute(t);
    }

    public void execute(Runnable t) {
        executor.execute(t);
    }

    public int getQueueSize() {
        return executor.getQueue().size();
    }

    /**
     * 异步提交返回 Future
     * Future.get()可获得返回结果
     *
     * @return
     */
    public Future<?> submit(Runnable t) {
        return executor.submit(t);
    }

    /**
     * 异步提交返回 Future
     * Future.get()可获得返回结果
     *
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Future<T> submit(Callable<T> t) {
        return getExecutor().submit(t);
    }

    /**
     * 销毁线程池
     */
    public void shutdown() {
        getExecutor().shutdown();
    }

    /**
     * 阻塞，直到线程池里所有任务结束
     *
     * @param timeout
     * @param unit
     */
    public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        log.info("Thread pool ,awaitTermination started, please wait till all the jobs complete.");
        executor.awaitTermination(timeout, unit);
    }
}
