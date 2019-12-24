package com.light.rain.router;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RouterRequest {

    private Object[] args;

    private String path;

    private String methodType;





}
