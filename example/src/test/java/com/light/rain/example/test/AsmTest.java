package com.light.rain.example.test;

import com.light.rain.example.http.server.impl.FisherManImpl;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class AsmTest {


    @Test
    public void test1() throws IOException {

        Class<FisherManImpl> fisherManClass = FisherManImpl.class;


        Map<Member, String[]> map = new ConcurrentHashMap<>(32);

        ClassReader reader = new ClassReader(FisherManImpl.class.getName());
        ClassPrinter classPrinter = new ClassPrinter(fisherManClass,map);

        reader.accept(classPrinter,0);

        map.forEach((k,v)->{

            log.info("k:[{}];v:[{}]",k,v);



        });







    }


}
