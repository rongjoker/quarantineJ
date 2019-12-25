package com.light.rain.util;

import com.light.rain.asm.ClassPrinter;
import com.light.rain.router.RouterRequest;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Log4j2
public class RouterUtil {

    private final static Map<Class<?>, Map<Member, String[]>> parameterNamesCache = new ConcurrentHashMap<>(32);


    public static String[] params(Method method) throws IOException {

        Class<?> declaringClass = method.getDeclaringClass();
        Map<Member, String[]> map = RouterUtil.parameterNamesCache.get(declaringClass);

        if (map == null) {
            map = new ConcurrentHashMap<>(32);

            ClassReader reader = new ClassReader(declaringClass.getName());
            ClassPrinter classPrinter = new ClassPrinter(declaringClass,map);

            reader.accept(classPrinter,0);
            RouterUtil.parameterNamesCache.put(declaringClass, map);
        }

        return map.get(method);
    }

    public static RouterRequest req(Method method,RouterRequest routerRequest) throws Exception {

        Parameter[] parameters = method.getParameters();

        String[] params = params(method);

        Map<String, String> queryString = routerRequest.getQueryString();

        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            String  className = parameter.getType().getName();
            String val = queryString.get(params[i]);

            if(className.equals("java.lang.String")){
                args[i] = queryString.get(params[i]);
            }else {
                Class<?> classByName = ReflectionUtils.getClassByName(className, RouterUtil.class.getClassLoader());
                args[i] = ReflectionUtils.convertDataByClass(val,classByName);
            }
        }

        routerRequest.setArgs(args);

        return routerRequest;
    }





}
