package com.light.rain.root;

import com.light.rain.router.RouterRequest;

@FunctionalInterface
public interface IDispatcher {

    Object doServlet(RouterRequest req) throws Exception;

}
