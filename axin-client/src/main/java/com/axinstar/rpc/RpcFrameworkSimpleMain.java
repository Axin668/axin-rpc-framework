package com.axinstar.rpc;

/**
 * @author axin
 * @since 2024/03/30
 */
public class RpcFrameworkSimpleMain {

    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1", 9999);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
