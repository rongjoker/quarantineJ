package com.light.rain.sevrlet;

import org.lr.concurrent.CommonRejectedExecutionHandler;
import org.lr.concurrent.CommonThreadFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BioServer implements ServerBase {

    private ServerSocket serverSocket;

    private boolean isRunning;

    private ThreadPoolExecutor threadPoolExecutor;



    @Override
    public void start(int port) {

        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;

            threadPoolExecutor = new ThreadPoolExecutor(5, 6, 2, TimeUnit.SECONDS
                    , new ArrayBlockingQueue<>(10)
                    , new CommonThreadFactory()
                    , new CommonRejectedExecutionHandler());

            System.out.println("start service with port "+port);

            listen();



        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void listen() {



        while (isRunning){

                try {
                    Socket accept = serverSocket.accept();
                    System.out.println("一个客户端建立了连接....");
                    //concurrent invocation

                    threadPoolExecutor.execute(new Dispatcher(accept));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        release();




    }

    void release(){

        if(null!=serverSocket){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            threadPoolExecutor.shutdown();
        }

    }


    public static void main(String[] args) {
        ServerBase serverBase = new BioServer();
        serverBase.start(9999);
    }


}
