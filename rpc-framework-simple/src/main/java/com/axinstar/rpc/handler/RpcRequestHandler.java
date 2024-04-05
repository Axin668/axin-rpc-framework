package com.axinstar.rpc.handler;

import com.axinstar.rpc.dto.RpcRequest;
import com.axinstar.rpc.dto.RpcResponse;
import com.axinstar.rpc.enumeration.RpcResponseCode;
import com.axinstar.rpc.exception.RpcException;
import com.axinstar.rpc.provider.ServiceProvider;
import com.axinstar.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RpcRequest 处理器
 *
 * @author axin
 * @since 2024/03/30
 */
public class RpcRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);
    private static final ServiceProvider SERVICE_PROVIDER;

    static {
        SERVICE_PROVIDER = new ServiceProviderImpl();
    }

    /**
     * 处理 rpcRequest: 调用对应的方法, 然后返回方法执行结果
     */
    public Object handle(RpcRequest rpcRequest) {
        Object result;
        // 通过注册中心获取到目标类(客户端需要调用类)
        Object service = SERVICE_PROVIDER.getServiceProvider(rpcRequest.getInterfaceName());
        result = invokeTargetMethod(rpcRequest, service);
        logger.info("service:{} successful invoke method:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        return result;
    }

    /**
     * 根据 rpcRequest 和 service 对象特定的方法并返回结果
     *
     * @param rpcRequest    客户端请求
     * @param service       提供服务的对象
     * @return
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service){
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            if (method == null) {
                return RpcResponse.fail(RpcResponseCode.NOT_FOUND_METHOD);
            }
            result = method.invoke(service, rpcRequest.getParameters());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}

