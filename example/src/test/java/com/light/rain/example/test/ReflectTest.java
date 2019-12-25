package com.light.rain.example.test;

import com.light.rain.example.http.server.impl.FisherManImpl;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Log4j2
public class ReflectTest {

    @Test
    public void testParams(){

        Class<FisherManImpl> fisherManClass = FisherManImpl.class;

        Method[] declaredMethods = fisherManClass.getDeclaredMethods();

        for (Method declaredMethod : declaredMethods) {

            Parameter[] parameters = declaredMethod.getParameters();

            log.info("parameters:[{}]",parameters);

        }

        Method[] methods = fisherManClass.getMethods();

        log.info("declaredMethods:[{}];methods:[{}]",declaredMethods.length,methods.length);




    }
}
