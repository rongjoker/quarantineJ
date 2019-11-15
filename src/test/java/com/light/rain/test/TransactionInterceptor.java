package com.light.rain.test;

public class TransactionInterceptor implements Interceptor {


    private Foo foo;

    @Override
    public String toString() {
        return "TransactionInterceptor{" +
                "foo=" + foo +
                '}';
    }

    @Override
    public Interceptor invocation() {

        foo = new Foo().setAge(11).setName("i am eleven");

        return this;

    }
}
