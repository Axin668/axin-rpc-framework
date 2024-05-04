package com.axinstar.rpc.provider;

/**
 * 保存和提供服务实例对象. 服务端调用
 *
 * @author axin
 * @since 2024/04/05
 */
public interface ServiceProvider {

    /**
     * 保存服务实例对象和服务实例对象实现的接口类的对应关系
     *
     * @param service       服务实例对象
     * @param serviceClass  服务实例对象实现的接口类
     */
    void addServiceProvider(Object service, Class<?> serviceClass);

    /**
     * 获取服务实例对象
     *
     * @param serviceName   服务实例对象实现的接口类的类名
     * @return              服务实例对象
     */
    Object getServiceProvider(String serviceName);

    /**
     * 发布服务
     *
     * @param service 服务实例对象
     */
    void publishService(Object service);
}
