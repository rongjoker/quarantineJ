package com.light.rain.router;

import com.alibaba.fastjson.JSON;
import com.light.rain.annotation.RouterMethod;
import com.light.rain.dispatcher.DefaultHtmlDispatcher;
import com.light.rain.dispatcher.HttpStaticFileDispatcher;
import com.light.rain.guice.GuiceCollection;
import com.light.rain.root.IBuilder;
import com.light.rain.root.IDispatcher;
import com.light.rain.root.IFilter;
import com.light.rain.root.IFix;
import com.light.rain.util.HttpUtil;
import com.light.rain.util.ReflectionUtils;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由处理类
 * 支持极简的路由转发，也支持扩展的复杂业务代码逻辑
 *
 * @author rongjoker
 */
@Log4j2
public class RouterCollection implements IBuilder {

    @Setter
    private String protocol;


    private GuiceCollection guiceCollection;

//    private final Map<String ,Map<String,RouterInvoke>> handles = new HashMap<>();
//
//    public RouterInvoke getRouterInvokeFromReq(RouterRequest request){
//
//        return null!=handles.get(request.getPath())?handles.get(request.getPath()).get(request.getPath()):null;
//
//    }

    private final List<IFilter> filters = new ArrayList<>();

    private final Map<RouterRequest, IDispatcher> urlDispatcherMap = new HashMap<>();
    private final Map<String , HttpStaticFileDispatcher> fileDispatcherMap = new HashMap<>();
    private final Map<String, IFix> codeFixMap = new HashMap<>();

    public RouterCollection scan(String... basePackages) {

        guiceCollection = new GuiceCollection(basePackages);

        return this;
    }

    public RouterCollection filter(IFilter filter) {
        filters.add(filter);
        return this;
    }

    public RouterCollection asset(String url, HttpStaticFileDispatcher fileDispatcher) {
        fileDispatcherMap.put(url, fileDispatcher);
        return this;
    }

    public RouterCollection dispatcher(String url, IDispatcher dispatcher) {
        urlDispatcherMap.put(new RouterRequest(url, RouterMethod.GET.name()), dispatcher);
        return this;
    }

    public RouterCollection dispatcher(String url, String methodType, IDispatcher dispatcher) {
        urlDispatcherMap.put(new RouterRequest(url, methodType), dispatcher);
        return this;
    }


    public void process(final ChannelHandlerContext ctx, final FullHttpRequest req) {

        if (CollectionUtils.isNotEmpty(filters)) {//暂时只支持前置过滤
            filters.forEach(e -> e.filter(ctx, req));
        }

        String uri = req.uri();
        String method = req.method().name();


        RouterRequest routerRequest = new RouterRequest();
        routerRequest.setPath(protocol+ uri).setMethodType(method);

        try {
            routerRequest = HttpUtil.build(routerRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        log.info("ctx:[{}];uri:[{}:{}]", ctx.channel().remoteAddress(),method, uri);

        var iDispatcher = urlDispatcherMap.get(routerRequest);

        if (null != iDispatcher) {
            try {
                Object hello_world =  iDispatcher.doServlet(routerRequest);//core process

                String  toJSON = JSON.toJSONString(hello_world);

                var buf = Unpooled.copiedBuffer(toJSON.getBytes(CharsetUtil.UTF_8));
//                var bytes = toJSON.getBytes(CharsetUtil.UTF_8);
//                buf.writeBytes(bytes);
                DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.valueOf(200), buf);
                res.headers().add(HttpHeaderNames.CONTENT_TYPE, String.format("%s; charset=utf-8", "text/json"));
                res.headers().add(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
                ctx.writeAndFlush(res);



            } catch (Exception e) {

                e.printStackTrace();

                fix(ctx, req, 400);

            }
        } else {
            fix(ctx, req, 404);
        }


    }

    /**
     * 处理异常
     *
     * @param ctx
     * @param req
     * @param code 404，500 etc.
     */
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


        this.asset("/favicon.ico", new HttpStaticFileDispatcher());//静态文件加载器

        urlDispatcherMap.forEach((k, v) -> {
            log.info("[{}] : [{}]", k, v.getClass().getName());
        });

        if (null != guiceCollection) {
            guiceCollection.startInternal();//启动ioc容器

            List<RouterInvoke> methods = new ArrayList<>(guiceCollection.getEarlyDispatchers().size() << 1);

            guiceCollection.getEarlyDispatchers().forEach(e -> {
                ReflectionUtils.getRouterMethods(guiceCollection.getInjector().getInstance(e), methods);
            });

            methods.forEach(method -> {

                for (RouterMethod routerMethod : method.getRouterMethods()) {

                    RouterRequest routerRequest = new RouterRequest(method.getPath().startsWith("/")?method.getPath():"/"+method.getPath(), routerMethod.name());

                    if(null!=urlDispatcherMap.get(routerRequest)){
                        throw new IllegalArgumentException(routerRequest.getMethodType()+"   "+routerRequest.getPath() + " is duplicated");
                    }

                    urlDispatcherMap.put(routerRequest, new DefaultHtmlDispatcher(method));

                    log.info("[{}] : [{}#{}]", routerRequest, method.getObject().getClass().getName(),method.getMethod().getName());
                }

            });

        }






    }

    @Override
    public void stopInternal() {

    }
}
