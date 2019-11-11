package com.light.rain.sevrlet;

import java.io.IOException;
import java.net.Socket;

public class Dispatcher implements Runnable {

    private Socket accept;
    private Request request;
    private Response response ;

    public Dispatcher(Socket accept) {
        this.accept = accept;
        try {
            request =new Request(accept);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.response = new Response(accept);
    }

    @Override
    public void run() {

        if(null!=accept){
            try {

                response.print("you are "+request.getUrl());

                response.print(("\n"));
                response.print(("I am here"));
                response.pushToBrowser(200);

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                release();
            }
        }




    }

    void release(){
        if(null!=accept){
            try {
                accept.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
