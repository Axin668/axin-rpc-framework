package com.axinstar.rpc;

/**
 * @author axin
 * @since 2024/03/30
 */
public class RpcFrameworkSimpleMain {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService, 9999);
    }
}
