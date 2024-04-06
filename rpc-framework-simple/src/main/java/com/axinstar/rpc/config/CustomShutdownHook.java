package com.axinstar.rpc.config;

import com.axinstar.rpc.utils.concurrent.ThreadPoolFactoryUtils;
import com.axinstar.rpc.utils.zk.CuratorUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
 * 当服务端(Provider)关闭的时候做一些事情, 比如说取消注册所有服务
 *
 * @author axin
 * @since 2024/04/06
 */
@Slf4j
public class CustomShutdownHook {

    private final ExecutorService threadPool = ThreadPoolFactoryUtils.createDefaultThreadPool("custom-shutdown-hook-rpc-pool");
    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CuratorUtils.clearRegistry();
            threadPool.shutdown();
        }));
    }
}
