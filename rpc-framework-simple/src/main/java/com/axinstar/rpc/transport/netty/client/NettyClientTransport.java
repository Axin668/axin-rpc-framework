package com.axinstar.rpc.transport.netty.client;

import com.axinstar.rpc.dto.RpcRequest;
import com.axinstar.rpc.dto.RpcResponse;
import com.axinstar.rpc.registry.ServiceDiscovery;
import com.axinstar.rpc.registry.ZkServiceDiscovery;
import com.axinstar.rpc.transport.ClientTransport;
import com.axinstar.rpc.utils.checker.RpcMessageChecker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
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

    public NettyClientTransport() {
        this.serviceDiscovery = new ZkServiceDiscovery();
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress);
            if (!channel.isActive()) {
                NettyClient.close();
                return null;
            }
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: {}", rpcRequest);
                } else {
                    future.channel().close();
                    log.error("Send failed:", future.cause());
                }
            });
            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
            RpcResponse rpcResponse = channel.attr(key).get();
            log.info("client get rpcResponse from channel:{}", rpcResponse);
            // 校验 RpcResponse 和 RpcRequest
            RpcMessageChecker.check(rpcResponse, rpcRequest);
            result.set(rpcResponse.getData());
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }

        return result.get();
    }
}
