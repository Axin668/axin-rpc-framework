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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 基于 Netty 传输 RpcRequest
 *
 * @author axin
 * @since 2024/04/04
 */
public class NettyClientTransport implements ClientTransport {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientTransport.class);
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
            if (channel.isActive()) {
                channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        logger.info("client send message: {}", rpcRequest);
                    } else {
                        future.channel().close();
                        logger.error("Send failed:", future.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                logger.info("client get rpcResponse from channel:{}", rpcResponse);
                // 校验 RpcResponse 和 RpcRequest
                RpcMessageChecker.check(rpcResponse, rpcRequest);
                result.set(rpcResponse.getData());
            } else {
                NettyClient.close();
                System.exit(0);
            }
        } catch (InterruptedException e) {
            logger.error("occur exception when send rpc message from client:", e);
        }

        return result.get();
    }
}
