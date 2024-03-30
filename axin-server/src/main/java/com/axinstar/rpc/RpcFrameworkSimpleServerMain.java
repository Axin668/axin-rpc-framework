package com.axinstar.rpc;

/**
 * @author axin
 * @since 2024/03/30
 */
public class RpcFrameworkSimpleServerMain {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService, 9999);
        // TODO 修改实现方式, 通过map存放service解决只能注册一个service
        System.out.println("后边的不会执行");
        rpcServer.register(new HelloServiceImpl(), 9999);
    }
}
