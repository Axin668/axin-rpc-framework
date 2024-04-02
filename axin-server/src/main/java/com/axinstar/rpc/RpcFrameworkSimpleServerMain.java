package com.axinstar.rpc;

import com.axinstar.rpc.registry.DefaultServiceRegistry;
import com.axinstar.rpc.transport.socket.SocketRpcServer;

/**
 * @author axin
 * @since 2024/03/30
 */
public class RpcFrameworkSimpleServerMain {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        DefaultServiceRegistry defaultServiceRegistry = new DefaultServiceRegistry();
        // 手动注册
        defaultServiceRegistry.register(helloService);
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        socketRpcServer.start(9999);
    }
}
