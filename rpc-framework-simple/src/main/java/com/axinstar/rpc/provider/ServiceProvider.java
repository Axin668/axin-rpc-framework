package com.axinstar.rpc.provider;

import com.axinstar.rpc.annotation.RpcService;
import com.axinstar.rpc.entity.RpcServiceProperties;

/**
 * 保存和提供服务实例对象. 服务端调用
 *
 * @author axin
 * @since 2024/04/05
 */
public interface ServiceProvider {

    /**
     * @param service               service object
     * @param serviceClass          the interface class implemented by the service instance object
     * @param rpcServiceProperties  service related attributes
     */
    void addServiceProvider(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties);

    /**
     * @param rpcServiceProperties      service related attributes
     * @return                          服务实例对象
     */
    Object getServiceProvider(RpcServiceProperties rpcServiceProperties);

    /**
     * @param service               service object
     * @param rpcServiceProperties  service related attributes
     */
    void publishService(Object service, RpcServiceProperties rpcServiceProperties);

    /**
     * @param service service object
     */
    void publishService(Object service);
}
