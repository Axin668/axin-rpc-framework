package com.axinstar.rpc.utils.zk;

import com.axinstar.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * zookeeper 客户端 Curator 工具类
 *
 * @author axin
 * @since 2024/04/04
 */
@Slf4j
public final class CuratorUtils {

    private static final int BASE_SLEEP_TIME = 100;
    private static final int MAX_RETRIES = 3;
    private static final String CONNECT_STRING = "127.0.0.1:2181";
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
    private static final Map<String, List<String>> serviceAddressMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredPathSet = ConcurrentHashMap.newKeySet();
    private static final CuratorFramework zkClient;

    static {
        zkClient = getZkClient();
    }

    private CuratorUtils() {
    }

    /**
     * 创建持久化节点. 不同于临时节点, 持久化节点不会因为客户端断开连接而被删除
     *
     * @param path 节点路径
     */
    public static void createPersistentNode(final String path) {
        try {
            if (registeredPathSet.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("节点已经存在, 节点为:[{}]", path);
            } else {
                //eg: /my-rpc/com.axinstar.rpc.HelloService/127.0.0.1:9999
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("节点创建成功, 节点为:[{}]", path);
            }
            registeredPathSet.add(path);
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 获取某个子节点下的子节点, 也就是获取所有提供服务的生产者的地址
     *
     * @param serviceName 服务对象接口名 eg: com.axinstar.rpc.HelloService
     * @return 指定字节下的所有子节点
     */
    public static List<String> getChildrenNodes(final String serviceName) {
        if (serviceAddressMap.containsKey(serviceName)) {
            return serviceAddressMap.get(serviceName);
        }
        List<String> result;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + serviceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            serviceAddressMap.put(serviceName, result);
            registerWatcher(serviceName);
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e.getCause());
        }
        return result;
    }

    /**
     * 清空注册中心的数据
     */
    public static void clearRegistry() {
        registeredPathSet.stream().parallel().forEach(p -> {
            try {
                zkClient.delete().forPath(p);
            } catch (Exception e) {
                throw new RpcException(e.getMessage(), e.getCause());
            }
        });
        log.info("服务端(Provider) 所有注册的服务都被清空:[{}]", registeredPathSet.toString());
    }

    private static CuratorFramework getZkClient() {
        // 重试策略, 重试5次, 并在两次重试之间等待100毫秒, 以防出现连接问题.
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                // 要连接的服务器(可以是服务器列表)
                .connectString(CONNECT_STRING)
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework.start();
        return curatorFramework;
    }

    /**
     * 注册监听指定节点
     *
     * @param serviceName   服务对象接口名 eg:com.axinstar.rpc.HelloService
     */
    private static void registerWatcher(String serviceName) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + serviceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(CuratorUtils.zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            serviceAddressMap.put(serviceName, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e.getCause());
        }
    }
}
