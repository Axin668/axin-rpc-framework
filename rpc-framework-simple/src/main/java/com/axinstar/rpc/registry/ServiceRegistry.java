package com.axinstar.rpc.registry;

/**
 * @author axin
 * @since 2024/04/01
 */
public interface ServiceRegistry {

    <T> void register(T service);

    Object getService(String serviceName);
}
