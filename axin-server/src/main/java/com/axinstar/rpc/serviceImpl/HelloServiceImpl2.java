package com.axinstar.rpc.serviceImpl;

import com.axinstar.rpc.Hello;
import com.axinstar.rpc.HelloService;
import com.axinstar.rpc.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author axin
 * @since 2024/05/05
 */
@Slf4j
@RpcService(group = "test2", version = "version1")
public class HelloServiceImpl2 implements HelloService {

    static {
        System.out.println("HelloServiceImpl2被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl2收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl2返回: {}", result);
        return result;
    }
}
