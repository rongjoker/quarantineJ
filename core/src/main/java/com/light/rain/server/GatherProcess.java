package com.light.rain.server;

import com.light.rain.dispatcher.HttpStaticFileDispatcher;
import com.light.rain.guice.GuiceCollection;
import com.light.rain.root.IBuilder;
import com.light.rain.root.IDispatcher;
import com.light.rain.root.IFilter;
import com.light.rain.root.IFix;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class GatherProcess implements IBuilder {

    private GuiceCollection guiceCollection;

    private List<IFilter> filters = new ArrayList<>();

    private Map<String, IDispatcher> urlDispatcherMap = new HashMap<>();
    private Map<String, IFix> codeFixMap = new HashMap<>();


    public GatherProcess scan(String... basePackages){

        guiceCollection = new GuiceCollection(basePackages);


        return this;
    }

    public GatherProcess filter(IFilter filter) {
        filters.add(filter);
        return this;
    }

    public GatherProcess dispatcher(String url, IDispatcher dispatcher) {
        urlDispatcherMap.put(url, dispatcher);
        return this;
    }


    public void process(final ChannelHandlerContext ctx, final FullHttpRequest req) {

        if (CollectionUtils.isNotEmpty(filters)) {//暂时只支持前置过滤
            filters.forEach(e -> e.filter(ctx, req));
        }

        String uri = req.uri();

        //@TODO ioc容器

        log.info("ctx:[{}];uri:[{}]", ctx.channel().remoteAddress(), uri);

        var iDispatcher = urlDispatcherMap.get(uri);

        if (null != iDispatcher) {
            try {
                iDispatcher.doServlet(ctx, req);//core process
            } catch (Exception e) {

                e.printStackTrace();

                fix(ctx, req, 400);

            }
        }
        else {
            fix(ctx, req, 404);
        }


    }

    private void fix(final ChannelHandlerContext ctx, final FullHttpRequest req, int code) {

        IFix fix = codeFixMap.get(code);
        if (null != fix)
            fix.fix(ctx, req);
        else {

            String message = "请求错误！";
            var buf = ByteBufAllocator.DEFAULT.buffer();
            var bytes = message.getBytes(CharsetUtil.UTF_8);
            buf.writeBytes(bytes);
            DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.valueOf(code), buf);
            res.headers().add(HttpHeaderNames.CONTENT_TYPE, String.format("%s; charset=utf-8", "text/json"));
            res.headers().add(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
            ctx.writeAndFlush(res);


        }

    }

    @Override
    public void startInternal() {

        this.dispatcher("/favicon_bk.ico",new HttpStaticFileDispatcher());

        urlDispatcherMap.forEach((k, v) -> {
            log.info("[{}] : [{}]", k, v.getClass().getName());
        });


        if(null!=guiceCollection)
            guiceCollection.startInternal();

    }

    @Override
    public void stopInternal() {
        guiceCollection.stopInternal();


    }
}
