package com.axinstar.rpc;

import com.axinstar.rpc.provider.ServiceProviderImpl;
import com.axinstar.rpc.remoting.transport.socket.SocketRpcServer;
import com.axinstar.rpc.serviceImpl.HelloServiceImpl;

/**
 * @author axin
 * @since 2024/03/30
 */
public class RpcFrameworkSimpleServerMain {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        SocketRpcServer socketRpcServer = new SocketRpcServer("127.0.0.1", 8080);
        socketRpcServer.start();
        ServiceProviderImpl serviceProvider = new ServiceProviderImpl();
        serviceProvider.publishService(helloService);
    }
}
