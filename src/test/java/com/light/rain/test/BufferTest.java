package com.light.rain.test;

import org.junit.Test;

import java.nio.ByteBuffer;

public class BufferTest {



    @Test
    public void testFunctionInterface(){

        BarAdvice barAdvice = new BarAdvice();

        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();

        barAdvice.assignable(11,transactionInterceptor::invocation);


    }


    @Test
    public void test1(){

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.clear();
        byte[] bytes = new byte[]{1,2,3};

        buffer.put(bytes);

        buffer.flip();
        System.out.println("buffer.remaining():"+buffer.remaining());

        for (int i = 0; i <buffer.remaining(); i++) {

            byte b = buffer.get(i);
            System.out.println(i+":"+b);
            System.out.println("buffer.remaining():"+buffer.remaining());
        }


        buffer.clear();






    }
}
