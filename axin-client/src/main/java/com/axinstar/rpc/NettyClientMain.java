package com.axinstar.rpc;

import com.axinstar.rpc.transport.ClientTransport;
import com.axinstar.rpc.proxy.RpcClientProxy;
import com.axinstar.rpc.transport.netty.client.NettyClientTransport;

import java.net.InetSocketAddress;

/**
 * @author axin
 * @since 2024/04/02
 */
public class NettyClientMain {

    public static void main(String[] args) {
        ClientTransport rpcClient = new NettyClientTransport();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        // 如需使用 assert 断言, 需要在 VM options 添加参数: -ea
        assert "Hello description is 222".equals(hello);
        String hello2 = helloService.hello(new Hello("666", "888"));
        System.out.println(hello2);
    }
}
