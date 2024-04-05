package com.axinstar.rpc.registry;

import com.axinstar.rpc.utils.zk.CuratorHelper;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 基于 zookeeper 实现服务注册中心
 *
 * @author axin
 * @since 2024/04/04
 */
public class ZkServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ZkServiceRegistry.class);
    private final CuratorFramework zkClient;

    public ZkServiceRegistry() {
        zkClient = CuratorHelper.getZkClient();
        zkClient.start();
    }

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        // 根节点下注册子节点: 服务
        StringBuilder servicePath = new StringBuilder(CuratorHelper.ZK_REGISTER_ROOT_PATH).append("/").append(serviceName);
        // 服务子节点下注册子节点: 服务地址
        servicePath.append(inetSocketAddress.toString());
        CuratorHelper.createEphemeralNode(zkClient, servicePath.toString());
        logger.info("节点创建成功, 节点为:{}", servicePath);
    }
}
