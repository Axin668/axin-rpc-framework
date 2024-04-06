package com.axinstar.rpc.remoting.transport.netty.client;

import com.axinstar.rpc.factory.SingletonFactory;
import com.axinstar.rpc.remoting.dto.RpcRequest;
import com.axinstar.rpc.remoting.dto.RpcResponse;
import com.axinstar.rpc.registry.ServiceDiscovery;
import com.axinstar.rpc.registry.ZkServiceDiscovery;
import com.axinstar.rpc.remoting.transport.ClientTransport;
import com.axinstar.rpc.remoting.dto.RpcMessageChecker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 基于 Netty 传输 RpcRequest
 *
 * @author axin
 * @since 2024/04/04
 */
@Slf4j
public class NettyClientTransport implements ClientTransport {

    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;

    public NettyClientTransport() {
        this.serviceDiscovery = new ZkServiceDiscovery();
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRpcRequest(RpcRequest rpcRequest) {
        // 构建返回值
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress);
            if (channel != null && channel.isActive()) {
                // 放入未处理的请求
                unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
                channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        log.info("client send message: {}", rpcRequest);
                    } else {
                        future.channel().close();
                        resultFuture.completeExceptionally(future.cause());
                        log.error("Send failed:", future.cause());
                    }
                });
            } else {
                throw new IllegalStateException();
            }
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
