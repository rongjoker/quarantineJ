package com.light.rain.root;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

@FunctionalInterface
public interface IFix {

    void fix(ChannelHandlerContext ctx, FullHttpRequest req);

}
