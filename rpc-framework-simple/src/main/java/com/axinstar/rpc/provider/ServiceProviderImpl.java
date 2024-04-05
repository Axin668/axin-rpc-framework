package com.axinstar.rpc.provider;

import com.axinstar.rpc.enumeration.RpcErrorMessageEnum;
import com.axinstar.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author axin
 * @since 2024/04/05
 */
public class ServiceProviderImpl implements ServiceProvider{

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    /**
     * 接口名和服务的对应关系
     * note: 处理一个接口被两个实现类实现的情况如何处理?
     */
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    /**
     * note: 可以修改为扫描注解注册
     */
    @Override
    public <T> void addServiceProvider(T service, Class<T> serviceClass) {
        String serviceName = serviceClass.getCanonicalName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("Add service: {} and interfaces:{}", serviceName, service.getClass().getInterfaces());
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
