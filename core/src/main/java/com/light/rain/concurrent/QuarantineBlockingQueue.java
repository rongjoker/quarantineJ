package com.light.rain.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * LinkedBlockingQueue配合ThreadPoolExecutor使用，适合cpu密集型的并发作业，
 * 因为队列容量是Integer.MAX,所以理论上只能开启corePoolSize个线，程无法激发maximumPoolSize
 * QuarantineBlockingQueue调整了逻辑，探测threadPoolExecutor的线程大小，能够开启maximumPoolSize个线程
 */
public class QuarantineBlockingQueue extends LinkedBlockingQueue<Runnable> {

    private volatile ThreadPoolExecutor threadPoolExecutor;

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    public boolean offer(Runnable o) {
        if (threadPoolExecutor==null)
            return super.offer(o);
        if (threadPoolExecutor.getPoolSize() == threadPoolExecutor.getMaximumPoolSize())
            return super.offer(o);
        if (threadPoolExecutor.getPoolSize()<threadPoolExecutor.getMaximumPoolSize()){//覆盖功能的一步，尽量创建线程，直到MaximumPoolSize,而不是把任务放入queue里
            return false;
        }


        return super.offer(o);
    }




}
