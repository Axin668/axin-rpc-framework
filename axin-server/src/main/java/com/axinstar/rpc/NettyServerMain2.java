package com.axinstar.rpc;

import com.axinstar.rpc.remoting.transport.netty.server.NettyServer;

/**
 * @author axin
 * @since 2024/04/13
 */
public class NettyServerMain2 {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyServer nettyServer = new NettyServer("127.0.0.1", 9998);
        nettyServer.publishService(helloService, HelloService.class);
    }
}
