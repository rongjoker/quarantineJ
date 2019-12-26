# quarantineJ
一个旨在替换掉tomcat+spring+mybatis的基于netty，支持ioc,router,aop,ddd,restful的极简后端框架。
我推崇约定大于配置，所以quarantineJ的目标是零配置，开箱即run...


# 目录
[**功能**](https://github.com/rongjoker/quarantineJ#功能)  
[**安装**](https://github.com/rongjoker/quarantineJ#安装)  
[**用法**](https://github.com/rongjoker/quarantineJ#用法)  
[**为什么quarantineJ**](https://github.com/rongjoker/quarantineJ#为什么quarantineJ)  
[**有关reactor模式**](https://github.com/rongjoker/quarantineJ#有关reactor模式)  
[**历史版本**](https://github.com/rongjoker/quarantineJ#历史版本)  

# 功能
* router,支持通用的restful请求(当前版本支持得还不完善，补全中)
* ioc,支持直接注入，也支持扫描注入（当前版本基于guice，后续可能会替换掉），考虑到实际场景，仅支持`singleton`的注入
* aop，基于asm开发，非常轻便（有bug，暂时撤下，2019.12.26追加）
* filter，目前支持叠加式过滤器
* 由于基于netty，所以netty-http支持的功能，quarantineJ都有，比如keep-alive等
* 旧的session实现得不好，已移除，新的session的支持还在开发中
* ddd(Domain Driven Design)的支持已经在路上，由于之前项目里开发过相似功能，所以这个功能可能会先上
* 所有模块都支持functional-programming，有一部分功能因为快速开发，抽象程度还不够，后续会优化
> 当前版本采用netty进行重构，翻掉了大量的代码，目前框架还处于0.1-beta版本，非常鲜嫩，很不完善。
> 如果你有任何新的需求或者想法意见,可以在issue中提出，欢迎star和fork~

# 安装
* 下载项目中的core,导入项目即可使用



# 用法
1. 参考  `example` 的案例，最简单的体验方式：
```bazaar

GatherBootstrap gatherBootstrap = new GatherBootstrap("127.0.0.1", 1234);

        gatherBootstrap.setGatherProcess(
                new RouterCollection()
                        .dispatcher("/test", (req) -> {
                                    log.info("不处理");

                                    return "just a test";

                                }
                        )
                        .dispatcher("/joker",new DemoDispatcher())
                        .filter((ctx, req) -> {
                                    log.info("测试过滤效果:[{}]",req.uri());

                                }
                        )
                

        ).startInternal();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            gatherBootstrap.stopInternal();

        }));

```
2. 基于ioc的完整体验方式:
2.1 添加scan路径：
```bazaar
        gatherBootstrap.setGatherProcess(
                new RouterCollection()
                        .dispatcher("/test", (req) -> {
                                    log.info("不处理");

                                    return "just a test";

                                }
                        )
                        .dispatcher("/joker",new DemoDispatcher())
                        .filter((ctx, req) -> {
                                    log.info("测试过滤效果:[{}]",req.uri());

                                }
                        )
                .scan("com.light.rain.example")

        ).startInternal();
```
2.2 添加支持扫描的bean注解:
```bazaar
@Singleton
public class FisherManImpl implements FisherMan, FisherMan2 {
```
2.3 具体的业务代码，对应router
```bazaar
    @Router(path="fisherman",method = RouterMethod.DELETE)
    public String delete(String boat) {
        log.info("i will say: [{}] with [{}]","i am an old man,i want to get a big fish",boat);

        return "i am an old man,i want to get a big fish with "+ boat;
    }
```
3. 配置好直接run即可


# 有关reactor模式
有关`reactor`模式,最好的说明是两张图，两张图胜过千言万语
<p align="center">
  <img src="https://github.com/rongjoker/quarantineJ/blob/joker_dev/core/src/main/document/1.png?raw=false" alt="reactor">
</p>
-
<p align="center">
  <img src="https://github.com/rongjoker/quarantineJ/blob/joker_dev/core/src/main/document/2.png?raw=false" alt="nio">
</p>

# 为什么quarantineJ
1. `spring-boot` 早就内嵌了`tomcat`和`jetty`,为什么还要开发quarantineJ？你的功能在spring面前就是个弟中弟，开发quarantineJ有什么意义？答：虽然现在微服务大行其道，功能完善，考虑周到，但是
实际工作中，往往我们需要一个轻量的很小的`restful`接口服务，`tomcat`和`spring`太强大，在这种场景下，相当于杀鸡用牛刀，基于`spring`的程序，即使功能很简单，最终生成的jar包也非常大，启动时间
在15秒起步，非常浪费。但是如果写一个简便的socket，也许你用1000行代码就可以写一个`nio-server`，但是功能又过于简略，没有实用价值。quarantineJ是这两种情况的取舍下的产物。
2. 也许你已经把`tomcat`的源码，`spring`的源码，甚至`mybatis`的源码都翻得熟透，但是如果你没有动手实现一个自己的`tomcat+spring/mybatis`，遇到细节的问题还是很困惑，比如一个最简单的问题，`spring`
的路由功能是如果将`queryString`和方法的参数一一对应的？比如`spring-ioc`是如何解决循环依赖的？`spring`动态代理为什么只触发一次构造方法？
3. 有很多奇妙的想法，无法在公司项目里实现，在quarantineJ里可以大胆的尝试，甚至随心所欲的按自己的理解来尝试，而不是对着框架的`guides`来敲代码，比如，我觉得`mybatis`太boring,想尝试下ddd...
4. 为什么要造轮子: [我们为什么要造轮子](https://github.com/rongjoker/rongjoker.github.io/blob/master/blog/whywemakecycle.md) 
5. `netty`真的很棒，无论是它的理念，还是它的代码。比如`spring`源码里，对性能完全无所谓，选择一种数据结构，没有把性能放在第一位考虑，而`netty`疯狂压榨性能，`O(logn)`不满意，一定要`O(1)`才舒服...
6. quarantineJ 来自我很喜欢的一个电影`The Aviator`(《飞行家》)，电影中男主 `Howard Hughes` 强迫症爆发的时候会一直拼`quarantine`:`Q-U-A-R-A-N-T-I-N-E`


# 历史版本
* 0.1-alpha 最简陋的版本，支持简化的router
* 0.1-beta 完善了router的支持，添加了扫描式的ioc

