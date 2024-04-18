package com.axinstar.rpc.spring.rpcservice;

import com.axinstar.rpc.ClientProxy;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author axin
 * @since 2024/04/18
 */
public class RpcServiceFactoryBean<T> implements FactoryBean<T> {

    private Class<T> rpcServiceInterface;

    public RpcServiceFactoryBean() {

    }

    public RpcServiceFactoryBean(Class<T> rpcServiceInterface) {
        this.rpcServiceInterface = rpcServiceInterface;
    }

    @Override
    public T getObject() throws Exception {
        if (rpcServiceInterface == null) {
            throw new IllegalStateException("");
        }
        return ClientProxy.getServiceProxy(rpcServiceInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return rpcServiceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
