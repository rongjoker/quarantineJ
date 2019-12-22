package com.light.rain.server;

import com.light.rain.concurrent.CommonRejectedExecutionHandler;
import com.light.rain.concurrent.CommonThreadFactory;
import com.light.rain.root.IBuilder;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;

import java.net.SocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author rongjoker
 */
@Log4j2
@ChannelHandler.Sharable
public class GatherHandler extends SimpleChannelInboundHandler<HttpObject> implements IBuilder {

    private final int threads = Runtime.getRuntime().availableProcessors();

    private ThreadPoolExecutor threadPoolExecutor;

    private GatherProcess process;

    public GatherHandler(GatherProcess process) {
        this.process = process;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {
        if(httpObject instanceof FullHttpRequest){
            FullHttpRequest fullHttpRequest = (FullHttpRequest)httpObject;

//            String s = ((FullHttpRequest) httpObject).headers().get("Connection");

//            if (!keepAlive) {
//                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//            } else {
//                response.headers().set(CONNECTION, Values.KEEP_ALIVE);
//                ctx.writeAndFlush(response);

            log.info("fc:[{}]",fullHttpRequest.getClass().getName());

            threadPoolExecutor.execute(()->{
                process.process(ctx,fullHttpRequest);

            });

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("ctx:[{}];ctx.channel().id:[{}] connect...",ctx.channel().remoteAddress(),ctx.channel().id().asLongText());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("ctx:[{}] close...",ctx.channel().remoteAddress());
//        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("ctx:[{}];error:[{}]",ctx.channel().remoteAddress(),cause.getMessage());
        cause.printStackTrace();
//        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        Channel currentChannel = ctx.channel();
        SocketAddress socketAddress = currentChannel.remoteAddress();

        if (evt instanceof IdleStateEvent) {//向下转型
            IdleStateEvent event = (IdleStateEvent) evt;

            switch (event.state()) {
                case READER_IDLE:
                    log.info("[{}]读空闲", socketAddress);
                    String message = "请求超时！";
                    var buf = ByteBufAllocator.DEFAULT.buffer();
                    var bytes = message.getBytes(CharsetUtil.UTF_8);
                    buf.writeBytes(bytes);
                    DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                            HttpResponseStatus.valueOf(400), buf);
                    res.headers().add(HttpHeaderNames.CONTENT_TYPE, String.format("%s; charset=utf-8", "text/json"));
                    res.headers().add(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
                    ctx.writeAndFlush(res);
                    break;
                case WRITER_IDLE:
                    log.info("[{}]写空闲", socketAddress);
                    break;
                case ALL_IDLE:
                    log.info("[{}读写空闲", socketAddress);
                    break;
            }

            currentChannel.close();//关闭

        }
    }

    @Override
    public void startInternal() {

        this.threadPoolExecutor = new ThreadPoolExecutor(threads, threads <<4, 60, TimeUnit.SECONDS
                , new LinkedBlockingQueue<>()
                , new CommonThreadFactory()
                , new CommonRejectedExecutionHandler());

        this.process.startInternal();


    }

    @Override
    public void stopInternal() {
        threadPoolExecutor.shutdown();
        try {
            threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.process.stopInternal();

    }
}
