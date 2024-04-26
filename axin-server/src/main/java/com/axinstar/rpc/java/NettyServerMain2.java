package com.axinstar.rpc.java;

import com.axinstar.rpc.HelloService;
import com.axinstar.rpc.HelloServiceImpl;
import com.axinstar.rpc.remoting.transport.netty.server.NettyServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author axin
 * @since 2024/04/13
 */
public class NettyServerMain2 {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyServer nettyServer = applicationContext.getBean(NettyServer.class);
        nettyServer.start();
        nettyServer.publishService(helloService, HelloService.class);
    }
}
