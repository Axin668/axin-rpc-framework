package com.axinstar.rpc.proxy;

import com.axinstar.rpc.dto.RpcRequest;
import com.axinstar.rpc.transport.ClientTransport;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * 动态代理类. 当动态代理对象调用一个方法的时候, 实际调用的是下面的 invoke 方法
 *
 * @author axin
 * @since 2024/03/30
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    /**
     * 用于发送请求给服务端, 对应socket和netty两种实现方式
     */
    private final ClientTransport clientTransport;

    public RpcClientProxy(ClientTransport clientTransport) {
        this.clientTransport = clientTransport;
    }

    /**
     * 通过 Proxy.newProxyInstance() 方法获取某个类的代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                this
        );
    }

    /**
     * 当使用代理对象调用方法(接口方法)的时候实际会调用到这个方法(invoke). 代理对象就是通过上面的 getProxy 方法获取到的对象
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("Call invoke method and invoked method: {}", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .build();
        return clientTransport.sendRpcRequest(rpcRequest);
    }
}
