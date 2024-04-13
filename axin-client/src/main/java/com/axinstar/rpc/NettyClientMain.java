package com.axinstar.rpc;

import com.axinstar.rpc.remoting.transport.ClientTransport;
import com.axinstar.rpc.proxy.RpcClientProxy;
import com.axinstar.rpc.remoting.transport.netty.client.NettyClientTransport;

/**
 * @author axin
 * @since 2024/04/02
 */
public class NettyClientMain {

    public static void main(String[] args) throws InterruptedException {
        ClientTransport rpcClient = new NettyClientTransport();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        // 如需使用 assert 断言, 需要在 VM options 添加参数: -ea
        assert "Hello description is 222".equals(hello);
        Thread.sleep(12000);
        for (int i = 0; i < 10; i ++ ) {
            helloService.hello(new Hello("111", "222"));
        }
    }
}
