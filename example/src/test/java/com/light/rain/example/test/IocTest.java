package com.light.rain.example.test;

import com.light.rain.util.ReflectionUtils;
import com.light.rain.util.ScanKlassUtil;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class IocTest {

    @Test
    public void testIOC(){

        ScanKlassUtil scanKlassUtil = new ScanKlassUtil();
        Map<Class, Class> map = scanKlassUtil.scanIOC("com.light.rain.example", new HashMap<>());

        map.forEach((k,v)->{
            System.out.println(k.getName()+"->"+v.getName());
            Object o = null;
            try {
                o = v.getConstructor().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            Method[] declaredMethods = new ReflectionUtils().getDeclaredMethods(v);
            for (Method declaredMethod : declaredMethods) {
                System.out.println(declaredMethod.getName());
                if("fish".equals(declaredMethod.getName())){
                    try {
                        declaredMethod.invoke(o,"xxxxxx");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }


            }

        });




    }


}
