package com.axinstar.rpc.registry;

import com.axinstar.rpc.utils.zk.CuratorUtils;
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
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + serviceName + inetSocketAddress.toString();
        CuratorUtils.createPersistentNode(servicePath);
        log.info("节点创建成功, 节点为:{}", servicePath);
    }
}
