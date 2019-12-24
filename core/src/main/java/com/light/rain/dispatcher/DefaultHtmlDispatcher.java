package com.light.rain.dispatcher;

import com.light.rain.root.IDispatcher;
import com.light.rain.router.RouterInvoke;
import com.light.rain.router.RouterRequest;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Log4j2
public class DefaultHtmlDispatcher implements IDispatcher {

    private RouterInvoke invoke;

    public DefaultHtmlDispatcher(RouterInvoke invoke) {
        this.invoke = invoke;
    }

    @Override
    public Object doServlet(RouterRequest request) throws Exception {
        Method method = invoke.getMethod();

        Parameter[] parameters = method.getParameters();

        for (Parameter parameter : parameters) {
            log.info("parameter.getName():[{}]",parameter.getName());


        }


        Object hello_world = invoke.getMethod().invoke(invoke.getObject(), request.getArgs());

        return hello_world;



    }
}
