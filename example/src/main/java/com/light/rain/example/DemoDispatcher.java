package com.light.rain.example;

import com.light.rain.root.IDispatcher;
import com.light.rain.router.RouterRequest;

public class DemoDispatcher implements IDispatcher {
    @Override
    public Object doServlet(RouterRequest request) {

        return "hello world";
    }
}
