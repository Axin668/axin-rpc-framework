package com.axinstar.rpc.serviceImpl;

import com.axinstar.rpc.Hello;
import com.axinstar.rpc.HelloService;
import com.axinstar.rpc.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author axin
 * @since 2024/03/30
 */
@Slf4j
@RpcService(group = "test1", version = "version1")
public class HelloServiceImpl implements HelloService {

    static {
        System.out.println("HelloServiceImpl被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}
