package com.axinstar.rpc.spring;

import com.axinstar.rpc.api.Hello;
import com.axinstar.rpc.api.HelloService;
import com.axinstar.rpc.spring.annotation.RpcServiceScan;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author axin
 * @since 2024/04/18
 */
public class ClientTest {

    @Test
    public void test() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(TestConfig.class);
        applicationContext.refresh();
        applicationContext.start();

        HelloService helloService = applicationContext.getBean(HelloService.class);
        Hello hello = Hello.builder()
                .message("test message")
                .description("test description")
                .build();
        String res = helloService.hello(hello);
        String expectedResult = "Hello description is " + hello.getDescription();
        Assert.assertEquals(expectedResult, res);
    }

    // @Configuration
    @RpcServiceScan("com.axinstar.rpc.api")
    public static class TestConfig {

    }
}
