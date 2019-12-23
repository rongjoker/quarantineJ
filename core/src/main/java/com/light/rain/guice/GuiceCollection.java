package com.light.rain.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.light.rain.root.IBuilder;
import com.light.rain.util.ScanKlassUtil;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class GuiceCollection implements IBuilder {

    private final List<Module> modules = new ArrayList<>();

    private String[] basePackages;

    private Injector injector;

    public GuiceCollection(String[] basePackages) {
        this.basePackages = basePackages;
    }

    public GuiceCollection() {
    }

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

        if(null!=basePackages){

            for (String basePackage : basePackages) {
                ScanKlassUtil scanKlassUtil = new ScanKlassUtil();
                Map<Class, Class> classClassMap = scanKlassUtil.scanIOC(basePackage,kvs);
            }
        }

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
                log.info("inject :[{}]-->[{}]",k.getName(),v.getName());

                bind(k).to(v);
            });
        }
    }
}
