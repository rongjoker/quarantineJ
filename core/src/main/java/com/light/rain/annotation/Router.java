package com.light.rain.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Router {

    String[] path() default {};

    RouterMethod[] method() default {RouterMethod.GET};


    String[] headers() default {};




}
