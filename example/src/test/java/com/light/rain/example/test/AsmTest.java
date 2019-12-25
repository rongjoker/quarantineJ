package com.light.rain.example.test;

import com.light.rain.example.http.server.impl.FisherManImpl;
import com.light.rain.util.RouterUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;

@Log4j2
public class AsmTest {


    @Test
    public void test1() throws IOException {

        Class<FisherManImpl> fisherManClass = FisherManImpl.class;

        for (Method declaredMethod : fisherManClass.getDeclaredMethods()) {
            String[] params = RouterUtil.params(declaredMethod);
            log.info("declaredMethod:[{}];params:[{}]",declaredMethod.getName(),params);
            log.info("params:[{}]",params);
            log.info("params2:[{}]",params.length);
        }











    }


}
