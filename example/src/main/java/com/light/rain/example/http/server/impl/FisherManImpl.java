package com.light.rain.example.http.server.impl;

import com.google.inject.Singleton;
import com.light.rain.annotation.Router;
import com.light.rain.annotation.RouterMethod;
import com.light.rain.example.http.server.FisherMan;
import com.light.rain.example.http.server.FisherMan2;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Singleton
public class FisherManImpl implements FisherMan, FisherMan2 {

    public FisherManImpl() {
        log.info("i am an old man");
    }

    @Router(path="fisherman2")
    public String http(String boat) {
        return this.fish(boat);
    }



    @Override
    @Router(path="fisherman")
    public String fish(String boat) {
        log.info("i will say: [{}] with [{}]","i am an old man,i want to get a big fish",boat);

        return "i am an old man,i want to get a big fish with "+ boat;
    }

    @Router(path="fisherman",method = RouterMethod.DELETE)
    public String delete(String boat) {
        log.info("i will say: [{}] with [{}]","i am an old man,i want to get a big fish",boat);

        return "i am an old man,i want to get a big fish with "+ boat;
    }




}
