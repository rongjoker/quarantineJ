package com.light.rain.test;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServletTest {


    @Test
    public void test1(){


        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            while (true){
                Socket accept = serverSocket.accept();
                System.out.println("i am here");
                System.out.println(new String(accept.getInputStream().readAllBytes()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }








}
