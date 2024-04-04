package com.axinstar.rpc;

import com.axinstar.rpc.transport.ClientTransport;
import com.axinstar.rpc.transport.RpcClientProxy;
import com.axinstar.rpc.transport.netty.client.NettyClientTransport;

import java.net.InetSocketAddress;

/**
 * @author axin
 * @since 2024/04/02
 */
public class NettyClientMain {

    public static void main(String[] args) {
        ClientTransport rpcClient = new NettyClientTransport(new InetSocketAddress("127.0.0.1", 9999));
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println("上面的调用卡住之后, 这里也不会调用了");
        helloService.hello(new Hello("111", "222"));
        // 如需使用 assert 断言, 需要在 VM options 添加参数: -ea
        assert "Hello description is 222".equals(hello);
    }
}
