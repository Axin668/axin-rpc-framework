package com.axinstar.rpc.provider;

/**
 * 保存和提供服务实例对象. 服务端调用
 *
 * @author axin
 * @since 2024/04/05
 */
public interface ServiceProvider {

    /**
     * 保存服务提供者
     */
    <T> void addServiceProvider(T service);

    /**
     * 获取服务提供者
     */
    Object getServiceProvider(String serviceName);
}
