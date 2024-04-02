package com.axinstar.rpc.registry;

import com.axinstar.rpc.enumeration.RpcErrorMessageEnum;
import com.axinstar.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author axin
 * @since 2024/04/01
 */
public class DefaultServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);

    /**
     *  接口名和服务的对应关系, TODO 处理一个接口被两个实现类实现的情况
     *  key: service/interface name
     *  value: service
     */
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    /**
     * TODO 修改为扫描注解注册
     * 将这个对象所有实现的接口都注册进去
     */
    @Override
    public synchronized <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("Add service: {} and interface:{}", serviceName, service.getClass().getInterfaces());
    }

    @Override
    public synchronized Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }
}
