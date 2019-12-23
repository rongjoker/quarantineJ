package com.light.rain.example.ioc.test;

import com.light.rain.util.ScanKlassUtil;
import org.junit.Test;

import java.util.HashMap;

public class IocTest {

    @Test
    public void testIOC(){

        ScanKlassUtil scanKlassUtil = new ScanKlassUtil();
        scanKlassUtil.scanIOC("com.light.rain.example",new HashMap<>());



    }


}
