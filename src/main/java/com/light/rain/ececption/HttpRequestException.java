package com.light.rain.ececption;

import lombok.Data;

@Data
public class HttpRequestException extends RuntimeException {

    private int code;

    private String message;

    private String uri;

    public HttpRequestException(int code, String uri, String message) {
        this.code = code;
        this.message = message;
        this.uri = uri;
    }
}
