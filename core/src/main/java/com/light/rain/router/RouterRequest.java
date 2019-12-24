package com.light.rain.router;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Objects;

@Data
@Accessors(chain = true)
public class RouterRequest {

    public RouterRequest(){}

    public RouterRequest(String path, String methodType) {
        this.path = path;
        this.methodType = methodType;
    }

    private Object[] args;

    Map<String, String> queryString;

    private String path;

    private String methodType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouterRequest that = (RouterRequest) o;
        return path.equals(that.path) &&
                methodType.equals(that.methodType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, methodType);
    }
}
