package com.light.rain.router;

import com.light.rain.annotation.RouterMethod;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

/**
 * 路由操作bean
 * @author rongjoker
 *
 */
@Data
@Accessors(chain = true)
public class RouterInvoke {

    private String path;

    private Object object;

    private Method method;

    private RouterMethod[] routerMethods;


}
