package com.axinstar.rpc.remoting.socket;

import com.axinstar.rpc.enumeration.RpcErrorMessageEnum;
import com.axinstar.rpc.exception.RpcException;
import com.axinstar.rpc.registry.ServiceRegistry;
import com.axinstar.rpc.remoting.RpcRequestHandler;
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

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private ExecutorService threadPool;
    private RpcRequestHandler rpcRequestHandler = new RpcRequestHandler();
    private final ServiceRegistry serviceRegistry;

    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadPool = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.MINUTES,
                workQueue,
                threadFactory
        );
    }

    public void start(int port) {
        try (ServerSocket server = new ServerSocket(port);) {
            logger.info("server starts...");
            Socket socket;
            while ((socket = server.accept()) != null) {
                logger.info("client connected");
                threadPool.execute(new RpcRequestHandlerRunnable(socket, rpcRequestHandler, serviceRegistry));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("occur IOException:", e);
        }
    }
}
