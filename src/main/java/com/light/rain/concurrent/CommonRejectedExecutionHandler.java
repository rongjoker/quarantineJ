package com.light.rain.concurrent;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Log4j2
public class CommonRejectedExecutionHandler implements RejectedExecutionHandler {


    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        log.info("Runnable:{};executor:{},put to queue",r.getClass().getSimpleName(),executor.getClass().getSimpleName());

        //     * Inserts the specified element into this queue, waiting if necessary
        //     * for space to become available.
        try {
            executor.getQueue().put(r);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
