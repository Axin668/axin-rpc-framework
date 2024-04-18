package com.axinstar.rpc.spring.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author axin
 * @since 2024/04/18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RpcServiceScannerRegistry.class)
public @interface RpcServiceScan {

    String value();
}
