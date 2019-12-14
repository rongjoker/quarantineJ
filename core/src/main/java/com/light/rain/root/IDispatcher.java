package com.light.rain.root;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

@FunctionalInterface
public interface IDispatcher {

    void doServlet(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception;



}
