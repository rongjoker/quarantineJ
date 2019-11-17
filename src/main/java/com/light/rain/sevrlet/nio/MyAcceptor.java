package com.light.rain.sevrlet.nio;

import java.nio.channels.SocketChannel;

public class MyAcceptor implements Runnable {

    private MyNioEndPoint endPoint;

    public MyAcceptor(MyNioEndPoint endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public void run() {

        try {
            SocketChannel channel = endPoint.serverSocketAccept();//serverSocketChannel.accept();

            // Disable blocking, polling will be used
            channel.configureBlocking(false);

            this.endPoint.getPoller().register(channel);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
