package com.axinstar.rpc.provider;

import com.axinstar.rpc.enumeration.RpcErrorMessage;
import com.axinstar.rpc.exception.RpcException;
import com.axinstar.rpc.registry.ServiceRegistry;
import com.axinstar.rpc.registry.zk.ZkServiceRegistry;
import com.axinstar.rpc.remoting.transport.netty.server.NettyServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
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
    private static final Map<String, Object> SERVICE_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_SERVICE = ConcurrentHashMap.newKeySet();
    private final ServiceRegistry serviceRegistry = new ZkServiceRegistry();

    /**
     * note: 可以修改为扫描注解注册
     */
    @Override
    public void addServiceProvider(Object service, Class<?> serviceClass) {
        String serviceName = serviceClass.getCanonicalName();
        if (REGISTERED_SERVICE.contains(serviceName)) {
            return;
        }
        REGISTERED_SERVICE.add(serviceName);
        SERVICE_MAP.put(serviceName, service);
        log.info("Add service: {} and interfaces:{}", serviceName, service.getClass().getInterfaces());
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = SERVICE_MAP.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcErrorMessage.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(Object service) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            Class<?> anInterface = service.getClass().getInterfaces()[0];
            this.addServiceProvider(service, anInterface);
            serviceRegistry.registerService(anInterface.getCanonicalName(), new InetSocketAddress(host, NettyServer.PORT));
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }
}
