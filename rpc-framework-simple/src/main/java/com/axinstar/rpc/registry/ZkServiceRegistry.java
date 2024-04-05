package com.axinstar.rpc.registry;

import com.axinstar.rpc.utils.zk.CuratorHelper;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 基于 zookeeper 实现服务注册中心
 *
 * @author axin
 * @since 2024/04/04
 */
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        // 根节点下注册子节点: 服务
        StringBuilder servicePath = new StringBuilder(CuratorHelper.ZK_REGISTER_ROOT_PATH).append("/").append(serviceName);
        // 服务子节点下注册子节点: 服务地址
        servicePath.append(inetSocketAddress.toString());
        CuratorHelper.createEphemeralNode(servicePath.toString());
        log.info("节点创建成功, 节点为:{}", servicePath);
    }
}
