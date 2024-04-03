package com.axinstar.rpc;

import com.axinstar.rpc.transport.RpcClient;
import com.axinstar.rpc.transport.RpcClientProxy;
import com.axinstar.rpc.transport.netty.client.NettyRpcClient;
import com.axinstar.rpc.transport.socket.SocketRpcClient;

/**
 * @author axin
 * @since 2024/03/30
 */
public class RpcFrameworkSimpleClientMain {

    public static void main(String[] args) {
        SocketRpcClient rpcClient = new SocketRpcClient("127.0.0.1", 9999);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
