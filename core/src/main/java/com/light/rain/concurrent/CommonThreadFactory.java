package com.light.rain.concurrent;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class CommonThreadFactory implements ThreadFactory {

    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        String threadName = r.getClass().getName() + "["+count.incrementAndGet()+"]";
        log.info("create worker thread:[{}]",threadName);
        t.setName(threadName);
        return t;
    }
}
