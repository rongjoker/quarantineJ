package com.light.rain.util;

import com.light.rain.annotation.Router;
import com.light.rain.router.RouterInvoke;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

@Log4j2
public class ReflectionUtils {

    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);


    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);

        Set<Class<?>> primitiveTypes = new HashSet<>(32);
        primitiveTypes.addAll(primitiveWrapperTypeMap.values());
        Collections.addAll(primitiveTypes, boolean[].class, byte[].class, char[].class,
                double[].class, float[].class, int[].class, long[].class, short[].class);
        primitiveTypes.add(void.class);
        for (Class<?> primitiveType : primitiveTypes) {
            primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
        }


    }

    public static Object convertDataByClass(String data,Class classz){

        if(classz.getName().equals("int")){
            if(null==data)return 0;
            else
                return Integer.valueOf(data);
        }

        return null;

    }


    /**
     * 根据类名获取类
     * @param className
     * @param classLoader
     * @return
     * @throws IllegalArgumentException
     */
    public static Class<?> getClassByName(String className, @Nullable ClassLoader classLoader)
            throws IllegalArgumentException {

        try {

            Class<?> primitiveClass = getClassByNamePrimitiveClassName(className);

            if(null!=primitiveClass)
                return primitiveClass;
            else
                return classLoader.loadClass(className);
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Could not find class [" + className + "]", ex);
        }
        catch (LinkageError err) {
            throw new IllegalArgumentException("Unresolvable class definition for class [" + className + "]", err);
        }
    }

    /**
     * 获取基础数据类型对象
     * @param name
     * @return
     */
    public static Class<?> getClassByNamePrimitiveClassName(@Nullable String name) {
        Class<?> result = null;
        if (name != null && name.length() <= 8) {
            result = primitiveTypeNameMap.get(name);
        }
        return result;
    }



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

                            log.debug("[{}];method:[{}#{}]",path,clazz.getName(),declaredMethod.getName());
                        }
                    }

                    cache.add(clazz);
                }

            }
        }

        return methods;

    }


}
