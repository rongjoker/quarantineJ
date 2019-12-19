package com.light.rain.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.light.rain.root.IBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiceCollection implements IBuilder {

    private final List<Module> modules = new ArrayList<>();

    private Injector injector;

    Map<Class,Class> kvs = new HashMap<>();


    GuiceCollection add(Class i, Class s){
        kvs.put(i,s);
        return this;
    }


    GuiceCollection add(Map<Class,Class> kv){
        kv.forEach((i,s)->{
            kvs.put(i,s);
        });
        return this;
    }


    @Override
    public void startInternal() {

        modules.add(new SimpleModule(kvs));

        injector = Guice.createInjector(modules);

    }

    @Override
    public void stopInternal() {

    }


    class SimpleModule extends AbstractModule {

        Map<Class,Class> kv;

        public SimpleModule(Map<Class, Class> kv) {
            this.kv = kv;
        }

        @Override
        protected void configure() {

            kv.forEach((k,v)->{
                bind(k).to(v);
            });
        }
    }
}
