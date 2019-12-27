package com.light.rain.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

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
