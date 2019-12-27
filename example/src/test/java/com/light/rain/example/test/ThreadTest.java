package com.light.rain.example.test;

import com.light.rain.concurrent.CommonRejectedExecutionHandler;
import com.light.rain.concurrent.CommonThreadFactory;
import com.light.rain.concurrent.QuarantineBlockingQueue;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class ThreadTest {



    @Test
    public void test1(){

        ThreadPoolExecutor threadPoolExecutor = null;

        QuarantineBlockingQueue workQueue = new QuarantineBlockingQueue();

        int threads = 5;
        threadPoolExecutor = new ThreadPoolExecutor(threads, threads *2, 10, TimeUnit.SECONDS
                , workQueue
                , new CommonThreadFactory("test-handler")
                , new CommonRejectedExecutionHandler());

        workQueue.setThreadPoolExecutor(threadPoolExecutor);

        AtomicInteger atomicInteger = new AtomicInteger(0);

        long time1 = System.currentTimeMillis();


        for (int i = 0; i < 100; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(8);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("add runnable----[{}]",i);
            threadPoolExecutor.execute(()->{

                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                atomicInteger.incrementAndGet();



                log.info("Thread.currentThread().getName():[{}];atomicInteger----[{}]",Thread.currentThread().getName(),atomicInteger.get());
            });
        }

        long time2 = System.currentTimeMillis();

        log.info("consume----[{}]",(time2 - time1));


        for (;;){
            log.info("Active----[{}];threadPoolExecutor.getQueue().size():[{}]",threadPoolExecutor.getActiveCount(),threadPoolExecutor.getQueue().size());


//            if(atomicInteger.get()>99){
            if(threadPoolExecutor.getActiveCount()==0){
                break;
            }

            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }



}
