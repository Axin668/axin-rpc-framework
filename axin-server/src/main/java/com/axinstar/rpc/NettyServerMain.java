package com.axinstar.rpc;

import com.axinstar.rpc.registry.DefaultServiceRegistry;
import com.axinstar.rpc.transport.netty.server.NettyRpcServer;

/**
 * @author axin
 * @since 2024/04/02
 */
public class NettyServerMain {

    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        DefaultServiceRegistry defaultServiceRegistry = new DefaultServiceRegistry();
        // 手动注册
        defaultServiceRegistry.register(helloService);
        NettyRpcServer nettyRpcServer = new NettyRpcServer(9999);
        nettyRpcServer.run();
    }
}
