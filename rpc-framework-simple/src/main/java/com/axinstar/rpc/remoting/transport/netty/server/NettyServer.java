package com.axinstar.rpc.remoting.transport.netty.server;

import com.axinstar.rpc.config.CustomShutdownHook;
import com.axinstar.rpc.remoting.dto.RpcRequest;
import com.axinstar.rpc.remoting.dto.RpcResponse;
import com.axinstar.rpc.remoting.transport.netty.codec.kryo.NettyKryoDecoder;
import com.axinstar.rpc.remoting.transport.netty.codec.kryo.NettyKryoEncoder;
import com.axinstar.rpc.serialize.kryo.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * Server. Receive the client message, call the corresponding method according to the client message,
 * and the return the result to the client.
 *
 * @author axin
 * @since 2024/04/02
 */
@Slf4j
@Component
public class NettyServer implements InitializingBean {

    private final KryoSerializer kryoSerializer = new KryoSerializer();
    public static final int PORT = 9998;

    @SneakyThrows
    public void start() {
        String host = InetAddress.getLocalHost().getHostAddress();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 当客户端第一次进行请求的时候才会进行初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 30 秒之内没有收到客户端请求的话就关闭连接
                            ch.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
                            ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    })
                    // TCP默认开启了 Nagle 算法, 该算法的作用是尽可能的发送大数据快, 减少网络传输. TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法.
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 表示系统用于临时存放已完成三次握手的请求的队列的最大长度, 如果连接建立频繁, 服务器创建新连接较慢, 可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128);
            // 绑定端口, 同步等待绑定成功
            ChannelFuture f = b.bind(host, PORT).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("occur exception when start server:", e);
        } finally {
            log.info("shutdown bossGroup and workGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * Called after setting all bean properties
     */
    @Override
    public void afterPropertiesSet() {
        CustomShutdownHook.getCustomShutdownHook().clearAll();
    }
}
