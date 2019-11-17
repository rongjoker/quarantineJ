package com.light.rain.test.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.junit.Test;

public class NettyTestMain {

    // 循环事件处理组
    static final EventLoopGroup bossGroup = new NioEventLoopGroup();
    static final EventLoopGroup workerGroup = new NioEventLoopGroup();







    @Test
    public void test1(){

        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup,workerGroup);
        server.channel(NioServerSocketChannel.class);
        server.childHandler(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("frameDecoder",
                        new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder",
                        new LengthFieldPrepender(4));
                pipeline.addLast("decoder",
                        new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast("encoder",
                        new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast(new TcpServerHandler());
            }
        });

        server.option(ChannelOption.SO_BACKLOG, 128);
        server.childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            ChannelFuture cf = server.bind("localhost", 8180).sync();
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            shutdown();
        }

    }


    /**
     * 关闭
     */
    protected static void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


    /**
     * 服务器端消息处理
     */
    static class TcpServerHandler extends SimpleChannelInboundHandler {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            System.out.println("客户端消息>>>> " + msg);
            ctx.channel().writeAndFlush("您好，客户端，我是服务器端。");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx,
                                    Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

    }


}
