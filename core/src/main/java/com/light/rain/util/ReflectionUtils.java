package com.light.rain.util;

import com.light.rain.annotation.Router;
import com.light.rain.router.RouterInvoke;
import lombok.extern.log4j.Log4j2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ReflectionUtils {

    public static Method[] getDeclaredMethods(Class<?> clazz){

         Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            Annotation[] declaredAnnotations = declaredMethod.getDeclaredAnnotations();
            for (Annotation declaredAnnotation : declaredAnnotations) {
                log.info("[{}];declaredAnnotation:[{}]",clazz.getName(),declaredAnnotation);
            }
        }

         return declaredMethods;

    }

    private static List<Class<?>> cache = new ArrayList<>();


    public static List<RouterInvoke> getRouterMethods(Object object,List<RouterInvoke> methods){

        Class<?> clazz = object.getClass();

        if(cache.indexOf(clazz)!=-1){
            return methods;
        }

        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            Annotation[] declaredAnnotations = declaredMethod.getDeclaredAnnotations();
            for (Annotation declaredAnnotation : declaredAnnotations) {
                if(declaredAnnotation.annotationType() == Router.class){

                    Router router = (Router)declaredAnnotation;

                    if(null!=router.path()){
                        for (String path : router.path()) {

                            RouterInvoke routerInvoke = new RouterInvoke();
                            routerInvoke.setObject(object).setMethod(declaredMethod).setPath(path).setRouterMethods(router.method());
                            methods.add(routerInvoke);

                            log.info("[{}];method:[{}#{}]",path,clazz.getName(),declaredMethod.getName());
                        }
                    }

                    cache.add(clazz);
                }

            }
        }

        return methods;

    }


}
