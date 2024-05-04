package com.axinstar.rpc.remoting.transport.netty.client;

import com.axinstar.rpc.remoting.dto.RpcRequest;
import com.axinstar.rpc.remoting.dto.RpcResponse;
import com.axinstar.rpc.serialize.kryo.KryoSerializer;
import com.axinstar.rpc.remoting.transport.netty.codec.kryo.NettyKryoDecoder;
import com.axinstar.rpc.remoting.transport.netty.codec.kryo.NettyKryoEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * initialize and close Bootstrap object
 *
 * @author axin
 * @since 2024/04/04
 */
@Slf4j
public class NettyClient {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    // initialize resources such as EventLoopGroup, Bootstrap
    public NettyClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        KryoSerializer kryoSerializer = new KryoSerializer();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // The timeout period of the connection
                // If this time is exceeded or the connection cannot be established, the connection fails
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // If no data is sent to the server within 15 seconds, a heartbeat request will be sent
                        ch.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        /*
                        * config custom serialization codec
                        * */
                        // ByteBuf -> RpcResponse
                        ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        // RpcRequest -> ByteBuf
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    /**
     * connect server and get the channel, so that you can send rpc message to server
     *
     * @param inetSocketAddress server address
     * @return the channel
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successfully!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
