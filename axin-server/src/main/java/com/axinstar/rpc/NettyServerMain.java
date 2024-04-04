package com.axinstar.rpc;

import com.axinstar.rpc.transport.netty.server.NettyServer;

/**
 * @author axin
 * @since 2024/04/02
 */
public class NettyServerMain {

    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        NettyServer nettyServer = new NettyServer("127.0.0.1", 9999);
        nettyServer.publishService(helloService, HelloService.class);
    }
}
