package com.axinstar.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 *
 * @author axin
 * @since 2024/04/05
 */
public interface ServiceDiscovery {

    /**
     * 查找服务
     *
     * @param serviceName   服务名称
     * @return              提供服务的地址
     */
    InetSocketAddress lookupService(String serviceName);
}
