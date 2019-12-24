package com.light.rain.util;

import com.light.rain.router.RouterRequest;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class HttpUtil {

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if(-1==idx){
                query_pairs.put(URLDecoder.decode(pair, "UTF-8"), null);
            }else
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));

        }
        return query_pairs;
    }


    public static RouterRequest build(RouterRequest routerRequest) throws UnsupportedEncodingException, MalformedURLException {

        URL url = new URL(routerRequest.getPath());
        routerRequest.setQueryString(splitQuery(url)).setPath(url.getPath());

        return routerRequest;
    }



}
