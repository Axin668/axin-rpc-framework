package com.axinstar.rpc.java;

import com.axinstar.rpc.HelloService;
import com.axinstar.rpc.HelloServiceImpl;
import com.axinstar.rpc.remoting.transport.netty.server.NettyServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author axin
 * @since 2024/04/02
 */
@ComponentScan("com.axinstar.rpc")
public class NettyServerMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyServer nettyServer = applicationContext.getBean(NettyServer.class);
        nettyServer.start();
    }
}
