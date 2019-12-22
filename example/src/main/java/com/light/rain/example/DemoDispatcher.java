package com.light.rain.example;

import com.alibaba.fastjson.JSON;
import com.light.rain.root.IDispatcher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class DemoDispatcher implements IDispatcher {
    @Override
    public void doServlet(ChannelHandlerContext ctx, FullHttpRequest req) {

        FooBar fooBar = new FooBar().setName("joker").setAge(30);
        String jsonString = JSON.toJSONString(fooBar);


        ByteBuf content = Unpooled.copiedBuffer(jsonString, CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/json");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());

//        try {
//            TimeUnit.SECONDS.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        ctx.writeAndFlush(response);

    }
}
