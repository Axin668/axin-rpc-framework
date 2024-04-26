package com.axinstar.rpc.provider;

import com.axinstar.rpc.enumeration.RpcErrorMessageEnum;
import com.axinstar.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现了 ServiceProvider 接口, 可以将其看做是一个保存和提供服务实例对象的示例
 *
 * @author axin
 * @since 2024/04/05
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    /**
     * 接口名和服务的对应关系
     * note: 处理一个接口被两个实现类实现的情况如何处理? (通过group分组)
     */
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    /**
     * note: 可以修改为扫描注解注册
     */
    @Override
    public void addServiceProvider(Object service, Class<?> serviceClass) {
        String serviceName = serviceClass.getCanonicalName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        log.info("Add service: {} and interfaces:{}", serviceName, service.getClass().getInterfaces());
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }
}
