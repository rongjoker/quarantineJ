package com.light.rain.concurrent;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Log4j2
public class CommonRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        log.info("Runnable:{};executor:{},add to queue fail", r.getClass().getSimpleName(), executor.getClass().getSimpleName());

        throw new RejectedExecutionException();

    }
}
