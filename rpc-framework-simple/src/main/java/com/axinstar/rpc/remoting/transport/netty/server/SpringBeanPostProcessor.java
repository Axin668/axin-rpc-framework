package com.axinstar.rpc.remoting.transport.netty.server;

import com.axinstar.rpc.annotation.RpcService;
import com.axinstar.rpc.factory.SingletonFactory;
import com.axinstar.rpc.provider.ServiceProvider;
import com.axinstar.rpc.provider.ServiceProviderImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * call this method before creating the bean to see if the class is annotated
 */
@Component
@Slf4j
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;

    public SpringBeanPostProcessor() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            serviceProvider.publishService(bean);
        }
        return bean;
    }
}
