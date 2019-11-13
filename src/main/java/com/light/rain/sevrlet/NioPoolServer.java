package com.light.rain.sevrlet;

import org.lr.concurrent.CommonRejectedExecutionHandler;
import org.lr.concurrent.CommonThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class NioPoolServer implements ServerBase {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    private boolean isRunning;

    private ThreadPoolExecutor threadPoolExecutor;

    public NioPoolServer(int port) {
        this.initial(port);
    }

    private Selector selector;

    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);


    public static void main(String[] args) {

        new NioPoolServer(9999).start();

    }

    @Override
    public ServerBase initial(int port) {

        try {
            selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);//none-blocking
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            isRunning = true;
            LOGGER.info("start service with port {}", port);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void start() {

        threadPoolExecutor = new ThreadPoolExecutor(5, 6, 2, TimeUnit.SECONDS
                , new ArrayBlockingQueue<>(10)
                , new CommonThreadFactory()
                , new CommonRejectedExecutionHandler());

        while (isRunning) {

            try {
                this.selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                handle(selectionKey);
            }
        }
    }

    private void handle(SelectionKey selectionKey) {


        if (selectionKey.isValid()) {
            if (selectionKey.isAcceptable()) {//有客户端欲连接，则建立通道，并监听读事件，
                try {
                    LOGGER.info("i am accept:{}", selectionKey);

                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel channel = serverSocketChannel.accept();//ReentrantLock同步锁
                    channel.configureBlocking(false);
                    channel.register(this.selector, SelectionKey.OP_READ);
                } catch (IOException e) {
                    e.printStackTrace();
                    selectionKey.cancel();
                }

            } else if (selectionKey.isReadable()) {

                    selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_READ);

                    threadPoolExecutor.execute(()->{

                        ByteBuffer readBufferTemp = ByteBuffer.allocate(1024);

                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();

                        try {
                            int readLength = clientChannel.read(readBufferTemp);
                            if (readLength == -1) {
                                selectionKey.channel().close();
                                selectionKey.cancel();
                                return;
                            }
                            this.readBuffer.flip();
                            byte[] bytes = new byte[readBufferTemp.remaining()];
                            readBufferTemp.get(bytes);
                            LOGGER.info("read form {}:{}", clientChannel.getRemoteAddress(), new String(bytes));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        NioHTTPSession httpSession = new NioHTTPSession(clientChannel);
                        selectionKey.attach(httpSession);

                        NioResponse response = new NioResponse();
                        response.setContent("i am joker with nio".getBytes());


                        httpSession.sendResponse(response);//直接写回去response

                        httpSession.close();


                    });












            } else if (selectionKey.isWritable()) {
                try {
                    write(selectionKey);
                } catch (IOException e) {
                    e.printStackTrace();
                    selectionKey.cancel();
                }
            }


        }


    }


    private void read(SelectionKey next) throws IOException {
        LOGGER.info("i am read:{}", next);

        readBuffer.clear();
        SocketChannel clientChannel = (SocketChannel) next.channel();
        int readLength = clientChannel.read(readBuffer);
        if (readLength == -1) {
            next.channel().close();
            next.cancel();
            return;
        }
        this.readBuffer.flip();
        byte[] bytes = new byte[this.readBuffer.remaining()];
        this.readBuffer.get(bytes);
        LOGGER.info("read form {}:{}", clientChannel.getRemoteAddress(), new String(bytes));


//        NioHTTPSession httpSession = new NioHTTPSession(clientChannel);
//        next.attach(httpSession);
//
//        NioResponse response = new NioResponse();
//        response.setContent("i am joker with nio".getBytes());
//
//
//        httpSession.sendResponse(response);
//
//        httpSession.close();

        LOGGER.info("next.attachment():{}", next.attachment());

        clientChannel.register(this.selector, SelectionKey.OP_WRITE);

    }


    private void write(SelectionKey next) throws IOException {
        LOGGER.info("i am write:{}", next);

        this.writeBuffer.clear();
        SocketChannel serverChannel = (SocketChannel) next.channel();
//        Scanner scanner = new Scanner(System.in);
//        LOGGER.info("put message to client > ");
//        String nextLine = scanner.nextLine();

        NioHTTPSession httpSession = new NioHTTPSession(serverChannel, writeBuffer);
        next.attach(httpSession);

        NioResponse response = new NioResponse();
        response.setContent("i am joker with nio".getBytes());
        httpSession.sendResponse(response);

//        httpSession.close();


        int limit = writeBuffer.position();
        // 它并没有清除数据，只是把position设置第一个位置上
        // 但是他會把limit 設置為buffer的capacity
        writeBuffer.clear();
        int bodyLength = limit - writeBuffer.position();


        serverChannel.close();


//        String nextLine = "i am joker";
//            writeBuffer.put(nextLine.getBytes("UTF-8"));
//            writeBuffer.flip();
//            serverChannel.write(writeBuffer);

//            serverChannel.register(this.selector,SelectionKey.OP_READ);


    }


}
