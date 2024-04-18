package com.axinstar.rpc;

import lombok.extern.slf4j.Slf4j;

/**
 * @author axin
 * @since 2024/03/30
 */
@Slf4j
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}
