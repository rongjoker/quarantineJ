package com.light.rain.sevrlet.nio;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class MyPoller implements Runnable {

    private final Logger LOGGER = LogManager.getLogger(getClass());

    private Selector selector;

    private volatile int keyCount = 0;

    public Selector getSelector() { return selector; }

    public MyPoller() throws IOException {
        this.selector = Selector.open();
    }

    public void register(SocketChannel channel) throws ClosedChannelException {
        channel.register(selector,SelectionKey.OP_READ);

    }




    @Override
    public void run() {

        try {
            keyCount = selector.selectNow();

            Iterator<SelectionKey> iterator =
                    keyCount > 0 ? selector.selectedKeys().iterator() : null;

            // Walk through the collection of ready keys and dispatch
            // any active event.
            while (iterator != null && iterator.hasNext()) {
                SelectionKey sk = iterator.next();
//                NioSocketWrapper socketWrapper = (NioSocketWrapper) sk.attachment();
                // Attachment may be null if another thread has called
                // cancelledKey()
//                if (socketWrapper == null) {
//                    iterator.remove();
//                } else {
//                    log.info("iterator the selector selectedKeys:常规的nio操作，迭代多路复用器");
//                    iterator.remove();
//                    processKey(sk, socketWrapper);
//                }

                LOGGER.info("iterator the selector selectedKeys:常规的nio操作，迭代多路复用器");
                iterator.remove();
                processKey(sk,sk.attachment());

                iterator.remove();

            }







        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void processKey(SelectionKey sk,Object socketWrapper) {
        if (sk.isValid() && socketWrapper != null) {
            if (sk.isReadable() || sk.isWritable()) {
//                if (socketWrapper.getSendfileData() != null) {
//                    processSendfile(sk, socketWrapper, false);
//                } else {
//                    unreg(sk, socketWrapper, sk.readyOps());
//                    boolean closeSocket = false;
//                    // Read goes before write
//                    if (sk.isReadable()) {//read
//                        log.info("-----read----");
//                        if (socketWrapper.readOperation != null) {
//                            if (!socketWrapper.readOperation.process()) {
//                                closeSocket = true;
//                            }
//                        } else if (!processSocket(socketWrapper, SocketEvent.OPEN_READ, true)) {
//                            closeSocket = true;
//                        }
//                    }
//                    if (!closeSocket && sk.isWritable()) {//write
//                        log.info("-----write----");
//                        if (socketWrapper.writeOperation != null) {
//                            if (!socketWrapper.writeOperation.process()) {
//                                closeSocket = true;
//                            }
//                        } else if (!processSocket(socketWrapper, SocketEvent.OPEN_WRITE, true)) {
//                            closeSocket = true;
//                        }
//                    }
//                    if (closeSocket) {
//                        cancelledKey(sk, socketWrapper);
//                    }
//                }
            }
        }
    }
}
