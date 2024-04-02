package com.axinstar.rpc;

import com.axinstar.rpc.transport.RpcClientProxy;
import com.axinstar.rpc.transport.netty.NettyRpcClient;

/**
 * @author axin
 * @since 2024/04/02
 */
public class NettyClientMain {

    public static void main(String[] args) {
        NettyRpcClient rpcClient = new NettyRpcClient("127.0.0.1", 9999);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
