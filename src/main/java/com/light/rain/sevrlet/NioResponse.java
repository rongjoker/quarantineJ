package com.light.rain.sevrlet;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class NioResponse {

    private String version = "HTTP/1.1";
    private int responseCode = 200;
    private String responseReason = "OK";
    private Map<String, String> headers = new LinkedHashMap<String, String>();
    private byte[] content;

    public void addDefaultHeaders() {
        headers.put("Date", new Date().toString());
        headers.put("Server", "Java NIO Webserver by joker");
        headers.put("Connection", "close");
        headers.put("Content-Length", Integer.toString(content.length));
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseReason() {
        return responseReason;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public byte[] getContent() {
        return content;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setResponseReason(String responseReason) {
        this.responseReason = responseReason;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getVersion() {
        return version;
    }


    public Map<String, String> getHeaders() {
        return headers;
    }
}
