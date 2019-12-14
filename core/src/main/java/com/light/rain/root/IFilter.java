package com.light.rain.root;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

@FunctionalInterface
public interface IFilter {

    void filter(ChannelHandlerContext ctx, FullHttpRequest req);
}
