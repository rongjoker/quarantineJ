package com.light.rain.dispatcher;

import com.light.rain.root.IDispatcher;
import com.light.rain.router.RouterInvoke;
import com.light.rain.router.RouterRequest;
import com.light.rain.util.RouterUtil;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;

@Log4j2
public class DefaultHtmlDispatcher implements IDispatcher {

    private RouterInvoke invoke;

    public DefaultHtmlDispatcher(RouterInvoke invoke) {
        this.invoke = invoke;
    }

    @Override
    public Object doServlet(RouterRequest request) throws Exception {
        Method method = invoke.getMethod();

        RouterUtil.req(method,request);

        return invoke.getMethod().invoke(invoke.getObject(), request.getArgs());

    }
}
