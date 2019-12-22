package com.light.rain.example;


import com.light.rain.server.GatherBootstrap;
import com.light.rain.server.GatherProcess;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DemoStart {

    public static void main(String[] args) {

        GatherBootstrap gatherBootstrap = new GatherBootstrap("127.0.0.1", 1234);

        gatherBootstrap.setGatherProcess(
                new GatherProcess()
                        .dispatcher("/test", (ctx, req) -> {
                                    log.info("不处理");

                                }
                        )
                        .dispatcher("/joker",new DemoDispatcher())
                        .filter((ctx, req) -> {
                                    log.info("测试过滤效果:[{}]",req.uri());

                                }
                        )
                .scan("com.light.rain.example")

        ).startInternal();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            gatherBootstrap.stopInternal();

        }));

    }

}
