package com.axinstar.rpc.registry;

import com.axinstar.rpc.loadbalance.LoadBalance;
import com.axinstar.rpc.loadbalance.RandomLoadBalance;
import com.axinstar.rpc.utils.zk.CuratorUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 基于 zookeeper 实现服务发现
 *
 * @author axin
 * @since 2024/04/05
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this.loadBalance = new RandomLoadBalance();
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(serviceName);
        // 负载均衡
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList);
        log.info("成功找到服务地址:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
