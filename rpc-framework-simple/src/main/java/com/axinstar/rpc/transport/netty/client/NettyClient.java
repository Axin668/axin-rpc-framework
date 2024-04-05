package com.axinstar.rpc.transport.netty.client;

import com.axinstar.rpc.dto.RpcRequest;
import com.axinstar.rpc.dto.RpcResponse;
import com.axinstar.rpc.serialize.kryo.KryoSerializer;
import com.axinstar.rpc.transport.netty.codec.NettyKryoDecoder;
import com.axinstar.rpc.transport.netty.codec.NettyKryoEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于初始化 和 关闭 Bootstrap 对象
 *
 * @author axin
 * @since 2024/04/04
 */
@Slf4j
public class NettyClient {

    private static final Bootstrap b;
    private static final EventLoopGroup eventLoopGroup;

    // 初始化相关资源比如 EventLoopGroup、Bootstrap
    static {
        eventLoopGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        KryoSerializer kryoSerializer = new KryoSerializer();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // 连接的超时时间, 超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 是否开启 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                // TCP默认开启了 Nagle 算法, 该算法的作用是尽可能地发送大数据块, 减少网络传输. TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法.
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        /*自定义序列化编解码器*/
                        // ByteBuf -> RpcResponse
                        ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        // RpcRequest -> ByteBuf
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    public NettyClient() {
    }

    public static void close() {
        log.info("call close method");
        eventLoopGroup.shutdownGracefully();
    }

    public static Bootstrap initializeBootstrap() {
        return b;
    }
}
