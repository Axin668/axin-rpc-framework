package com.axinstar.rpc;

import com.axinstar.rpc.transport.ClientTransport;
import com.axinstar.rpc.proxy.RpcClientProxy;
import com.axinstar.rpc.transport.socket.SocketRpcClient;

/**
 * @author axin
 * @since 2024/03/30
 */
public class RpcFrameworkSimpleClientMain {

    public static void main(String[] args) {
        ClientTransport clientTransport = new SocketRpcClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(clientTransport);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
