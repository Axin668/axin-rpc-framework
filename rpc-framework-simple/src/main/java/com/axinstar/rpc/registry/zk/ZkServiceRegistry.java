package com.axinstar.rpc.registry.zk;

import com.axinstar.rpc.registry.util.CuratorUtils;
import com.axinstar.rpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

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
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
        log.info("节点创建成功, 节点为:{}", servicePath);
    }
}
