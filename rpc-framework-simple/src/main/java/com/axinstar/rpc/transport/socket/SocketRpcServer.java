package com.axinstar.rpc.transport.socket;

import com.axinstar.rpc.provider.ServiceProvider;
import com.axinstar.rpc.provider.ServiceProviderImpl;
import com.axinstar.rpc.registry.ServiceRegistry;
import com.axinstar.rpc.registry.ZkServiceRegistry;
import com.axinstar.rpc.utils.concurrent.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author axin
 * @since 2024/03/30
 */
public class SocketRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(SocketRpcServer.class);
    private final ExecutorService threadPool;
    private final String host;
    private final int port;
    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    public SocketRpcServer(String host, int port) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-server-rpc-pool");
        serviceRegistry = new ZkServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
    }

    public <T> void publishService(T service, Class<T> serviceClass) {
        serviceProvider.addServiceProvider(service, serviceClass);
        serviceRegistry.registerService(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

    private void start() {
        try (ServerSocket server = new ServerSocket()) {
            server.bind(new InetSocketAddress(host, port));
            logger.info("server starts...");
            Socket socket;
            while ((socket = server.accept()) != null) {
                logger.info("client connected");
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("occur IOException:", e);
        }
    }
}
