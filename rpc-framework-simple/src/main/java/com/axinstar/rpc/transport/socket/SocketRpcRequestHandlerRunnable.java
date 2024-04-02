package com.axinstar.rpc.transport.socket;

import com.axinstar.rpc.dto.RpcRequest;
import com.axinstar.rpc.dto.RpcResponse;
import com.axinstar.rpc.registry.DefaultServiceRegistry;
import com.axinstar.rpc.registry.ServiceRegistry;
import com.axinstar.rpc.transport.RpcRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketRpcRequestHandlerRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SocketRpcRequestHandlerRunnable.class);
    private Socket socket;
    private static RpcRequestHandler rpcRequestHandler;
    private static ServiceRegistry serviceRegistry;
    static {
        rpcRequestHandler = new RpcRequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }

    public SocketRpcRequestHandlerRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = rpcRequestHandler.handle(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("occur exception:", e);
        }
    }
}
