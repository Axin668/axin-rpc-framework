package com.axinstar.rpc.remoting.transport.socket;

import com.axinstar.rpc.entity.RpcServiceProperties;
import com.axinstar.rpc.remoting.dto.RpcRequest;
import com.axinstar.rpc.exception.RpcException;
import com.axinstar.rpc.registry.ServiceDiscovery;
import com.axinstar.rpc.registry.zk.ZkServiceDiscovery;
import com.axinstar.rpc.remoting.transport.ClientTransport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 基于 Socket 传输 RpcRequest
 *
 * @author axin
 * @since 2024/04/05
 */
@AllArgsConstructor
@Slf4j
public class SocketRpcClient implements ClientTransport {

    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this.serviceDiscovery = new ZkServiceDiscovery();
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // build rpc service name by rpcRequest
        String rpcServiceName = RpcServiceProperties.builder()
                .serviceName(rpcRequest.getInterfaceName())
                .group(rpcRequest.getGroup())
                .version(rpcRequest.getVersion())
                .build().toRpcServiceName();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcServiceName);
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // 通过输出流发送数据到服务端
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // 从输入流中读取出 RpcResponse
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("调用服务失败:", e);
        }
    }
}
