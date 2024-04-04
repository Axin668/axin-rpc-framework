package com.axinstar.rpc.transport.socket;

import com.axinstar.rpc.utils.concurrent.ThreadPoolFactory;
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
public class SocketRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(SocketRpcServer.class);
    private final ExecutorService threadPool;

    public SocketRpcServer() {
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-server-rpc-pool");
    }

    public void start(int port) {
        try (ServerSocket server = new ServerSocket(port)) {
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
