package com.axinstar.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * RPC service annotation, marked on the service implementation class
 *
 * @author axin
 * @since 2024/05/05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Component
public @interface RpcService {

    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";
}
