package com.light.rain.sevrlet.nio;

import com.light.rain.sevrlet.ServerBase;
import org.lr.concurrent.CommonRejectedExecutionHandler;
import org.lr.concurrent.CommonThreadFactory;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyNioEndPoint implements ServerBase {

    private final Logger LOGGER = LogManager.getLogger(getClass());

    private boolean isRunning;

    private MyPoller poller;

    public MyPoller getPoller() {
        return poller;
    }

    private volatile ServerSocketChannel serverSocketChannel;

    private ThreadPoolExecutor threadPoolExecutor;

    protected SocketChannel serverSocketAccept() throws Exception {
        return serverSocketChannel.accept();
    }

    public MyNioEndPoint(int port) {
    }

    private int port;

    @Override
    public ServerBase initial(int port) {

        try {
            serverSocketChannel = ServerSocketChannel.open();

            // ※※※ 设置 ServerSocketChannel 为阻塞模式 ※※※
            serverSocketChannel.configureBlocking(true); //mimic APR behavior@TODO tomcat这里设置为true，存疑，跟进

            serverSocketChannel.socket().bind(new InetSocketAddress(port),100);//backlog设置为100,默认的连接等待队列长度是 100， 当超过 100 个时会拒绝服务

            this.port = port;

        } catch (IOException e) {
            e.printStackTrace();
        }





        return null;
    }

    @Override
    public void start() {

        threadPoolExecutor = new ThreadPoolExecutor(5, 6, 2, TimeUnit.SECONDS
                , new ArrayBlockingQueue<>(10)
                , new CommonThreadFactory()
                , new CommonRejectedExecutionHandler());

        try {

            isRunning = true;

            poller = new MyPoller();

            new Thread(poller).start();

            Thread.sleep(50);

            new Thread(new MyAcceptor(this)).start();



            LOGGER.info("start service with port {}", this.port);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
