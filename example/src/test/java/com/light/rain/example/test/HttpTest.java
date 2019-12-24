package com.light.rain.example.test;

import com.light.rain.util.HttpUtil;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class HttpTest {

    @Test
    public void test1() throws MalformedURLException, UnsupportedEncodingException {
        String u = "http://localhost:1234/fisherman?aka=123&sss";
         u = "http://localhost:1234/fisherman?aka=123&sss=";
        URL url = new URL(u);
        Map<String, String> stringStringMap = HttpUtil.splitQuery(url);
        stringStringMap.forEach((k,v)->{
            System.out.println(k+":"+v);
        });

    }
}
