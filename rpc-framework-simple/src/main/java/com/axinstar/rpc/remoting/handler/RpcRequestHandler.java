package com.axinstar.rpc.remoting.handler;

import com.axinstar.rpc.entity.RpcServiceProperties;
import com.axinstar.rpc.factory.SingletonFactory;
import com.axinstar.rpc.remoting.dto.RpcRequest;
import com.axinstar.rpc.exception.RpcException;
import com.axinstar.rpc.provider.ServiceProvider;
import com.axinstar.rpc.provider.ServiceProviderImpl;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RpcRequest 处理器
 *
 * @author axin
 * @since 2024/03/30
 */
@Slf4j
public class RpcRequestHandler {

    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    /**
     * 处理 rpcRequest: 调用对应的方法, 然后返回方法执行结果
     */
    public Object handle(RpcRequest rpcRequest) {
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .serviceName(rpcRequest.getInterfaceName())
                .version(rpcRequest.getVersion())
                .group(rpcRequest.getGroup())
                .build();
        // 通过注册中心获取到目标类(客户端需要调用类)
        Object service = serviceProvider.getServiceProvider(rpcServiceProperties);
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 根据 rpcRequest 和 service 对象特定的方法并返回结果
     *
     * @param rpcRequest    客户端请求
     * @param service       提供服务的对象
     * @return              目标方法执行的结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}

