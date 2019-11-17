package com.light.rain.test.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.junit.Test;

public class NettyClientTestMain {



    @Test
    public void test1(){
        try {
            NettyClientTestMain.sendMsg("127.0.0.1", 8180);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 初始化Bootstrap
     *
     * @return
     */
    public static final Bootstrap getBootstrap() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder",
                        new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder",
                        new LengthFieldPrepender(4));
                pipeline.addLast("decoder",
                        new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast("encoder",
                        new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast("handler", new TcpClientHandler());
            }
        });
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        return bootstrap;
    }

    /**
     * 客户器端消息处理
     */
    static class TcpClientHandler extends SimpleChannelInboundHandler {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            System.out.println("服务器端消息>>>> " + msg);
            Thread.sleep(2*1000);
            ctx.writeAndFlush("你好，我是客户端");
        }

    }

    /**
     * @param host
     * @param port
     * @return
     */
    public static final Channel getChannel(String host,int port){
        Channel channel = null;
        try {
            channel = getBootstrap().connect(host, port).sync().channel();
        } catch (Exception e) {
            return null;
        }
        return channel;
    }

    /**
     * @param ip
     * @param port
     * @throws Exception
     */
    public static void sendMsg(String ip,int port) throws Exception {
        Channel channel = getChannel(ip,port);
        if(channel!=null){
            channel.writeAndFlush("你好，我是客户端").sync();
        }else{
            System.err.println("消息发送失败，连接尚未建立成功！");
        }
    }




}
