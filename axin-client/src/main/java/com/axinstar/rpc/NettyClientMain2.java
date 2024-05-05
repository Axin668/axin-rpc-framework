package com.axinstar.rpc;

import com.axinstar.rpc.entity.RpcServiceProperties;
import com.axinstar.rpc.proxy.RpcClientProxy;
import com.axinstar.rpc.remoting.transport.netty.client.NettyClientTransport;

/**
 * @author axin
 * @since 2024/05/05
 */
public class NettyClientMain2 {

    public static void main(String[] args) throws InterruptedException {
        NettyClientTransport rpcClient = new NettyClientTransport();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2")
                .version("version1")
                .build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceProperties);
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
