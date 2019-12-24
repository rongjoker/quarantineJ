package com.light.rain.router;

import com.google.inject.Injector;
import com.light.rain.root.IBuilder;
import com.light.rain.util.ReflectionUtils;

import java.util.*;

public class RouterCollection implements IBuilder {

    private final Injector injector;

    private final Set<Class> earlyDispatchers;


    public RouterCollection(Injector injector, Set<Class> earlyDispatchers) {
        this.injector = injector;
        this.earlyDispatchers = earlyDispatchers;
    }

    private final Map<RouterRequest,RouterInvoke> handles = new HashMap<>();

    @Override
    public void startInternal() {

        List<RouterInvoke> methods = new ArrayList<>(earlyDispatchers.size()<<1);

        earlyDispatchers.forEach(e->{
            ReflectionUtils.getRouterMethods(injector.getInstance(e),methods);
        });

    }

    @Override
    public void stopInternal() {

    }
}
