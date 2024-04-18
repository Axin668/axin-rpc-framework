package com.axinstar.rpc;

import com.axinstar.rpc.proxy.RpcClientProxy;
import com.axinstar.rpc.remoting.transport.netty.client.NettyClient;
import com.axinstar.rpc.remoting.transport.netty.client.NettyClientTransport;

/**
 * @author axin
 * @since 2024/04/18
 * @description: 简单写一个方法获取代理对象, 其实应该在simple框架里边提供一个接口获取代理对象
 */
public class ClientProxy {

    public static <T> T getServiceProxy(Class<T> serviceClass) {
        NettyClientTransport rpcClient = new NettyClientTransport();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        return rpcClientProxy.getProxy(serviceClass);
    }
}
