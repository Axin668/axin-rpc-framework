package com.axinstar.rpc;

import com.axinstar.rpc.enumeration.RpcErrorMessageEnum;
import com.axinstar.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author axin
 * @since 2024/03/30
 */
public class RpcServer {

    private ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    public RpcServer() {
        // 线程池参数
        int corePoolSize = 10;
        int maximumPoolSize = 100;
        long keepAliveTime = 1;
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadPool = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.MINUTES,
                workQueue,
                threadFactory
        );
    }

    /**
     * 服务端主动注册服务
     * TODO 1. 定义一个 hashMap 存放相关的service
     *      2. 修改为扫描注解注册
     */
    public void register(Object service, int port) {
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_NULL);
        }
        try (ServerSocket server = new ServerSocket(port)) {
            logger.info("server starts...");
            Socket socket;
            while ((socket = server.accept()) != null) {
                logger.info("client connected");
                threadPool.execute(new ClientMessageHandlerThread(socket, service));
            }
        } catch (IOException e) {
            logger.error("occur IOException: ", e);
        }
    }
}
