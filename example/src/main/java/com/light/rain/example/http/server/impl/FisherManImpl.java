package com.light.rain.example.http.server.impl;

import com.google.inject.Singleton;
import com.light.rain.annotation.Router;
import com.light.rain.example.http.server.FisherMan;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Singleton
public class FisherManImpl implements FisherMan {



    @Router(path="fisherman")
    public String http(String boat) {
        return this.fish(boat);
    }



    @Override
    public String fish(String boat) {
        return "i am an old man,i want to get a big fish";
    }




}
