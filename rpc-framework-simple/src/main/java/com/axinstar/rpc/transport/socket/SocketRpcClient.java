package com.axinstar.rpc.transport.socket;

import com.axinstar.rpc.dto.RpcRequest;
import com.axinstar.rpc.dto.RpcResponse;
import com.axinstar.rpc.exception.RpcException;
import com.axinstar.rpc.registry.ServiceDiscovery;
import com.axinstar.rpc.registry.ZkServiceDiscovery;
import com.axinstar.rpc.transport.ClientTransport;
import com.axinstar.rpc.utils.checker.RpcMessageChecker;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class SocketRpcClient implements ClientTransport {
    private static final Logger logger = LoggerFactory.getLogger(SocketRpcClient.class);
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this.serviceDiscovery = new ZkServiceDiscovery();
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // 通过输出流发送数据到服务端
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // 从输入流中读取出 RpcResponse
            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
            // 校验 RpcResponse 和 RpcRequest
            RpcMessageChecker.check(rpcResponse, rpcRequest);
            return rpcResponse.getData();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("occur exception when send sendRpcRequest");
            throw new RpcException("调用服务失败:", e);
        }
    }
}
