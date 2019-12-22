package com.light.rain.server;

import com.light.rain.root.IBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.log4j.Log4j2;

/**
 * 服务器核心启动类，基于netty
 *
 * @author rongjoker
 */
@Log4j2
public class GatherBootstrap implements IBuilder {

    private final String host;

    private final int port;

    private GatherHandler gatherHandler;

    private ChannelFuture channelFuture = null;

    private NioEventLoopGroup boss = null;

    private NioEventLoopGroup worker = null;

    private GatherProcess gatherProcess;

    private int workThreads = Runtime.getRuntime().availableProcessors();

    public GatherBootstrap(String host, int port) {
        this.host = host;
        this.port = port;

    }


    public GatherBootstrap setGatherProcess(GatherProcess gatherProcess) {
        this.gatherProcess = gatherProcess;
        return this;
    }

    @Override
    public void startInternal() {

        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup(workThreads);

        gatherHandler = new GatherHandler(this.gatherProcess);

        gatherHandler.startInternal();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            final ChannelPipeline pipeline = socketChannel.pipeline();
//                            pipeline.addLast(new ReadTimeoutHandler(5));//timeout->5s
                            pipeline.addLast(new HttpServerCodec());//以块的形式进行写
                            pipeline.addLast(new ChunkedWriteHandler());//http请求是分段处理的
                            pipeline.addLast(new HttpObjectAggregator(1024 << 10));
                            //心跳检测,读、写、读写,默认是TimeUnit.SECONDS,触发IdleStateEvent，传递给pipeline下一个handle的UserEventTrigger
//                            pipeline.addLast(new IdleStateHandler(5,10,15, TimeUnit.SECONDS));
                            pipeline.addLast(gatherHandler);

                        }
                    })

            ;

            channelFuture = serverBootstrap.bind(host, port).sync();

            log.info("server start :[{}]",port);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void stopInternal() {

        channelFuture.channel().close();
        boss.shutdownGracefully();
        worker.shutdownGracefully();

        gatherHandler.stopInternal();

    }





}
