package com.axinstar.rpc.transport.netty.client;

import com.axinstar.rpc.dto.RpcRequest;
import com.axinstar.rpc.dto.RpcResponse;
import com.axinstar.rpc.serialize.kryo.KryoSerializer;
import com.axinstar.rpc.transport.RpcClient;
import com.axinstar.rpc.transport.netty.codec.NettyKryoDecoder;
import com.axinstar.rpc.transport.netty.codec.NettyKryoEncoder;
import com.axinstar.rpc.utils.checker.RpcMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端. 发送消息到服务端, 并接受服务端返回的方法执行结果
 *
 * @author axin
 * @since 2024/04/02
 */
public class NettyRpcClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcClient.class);
    private String host;
    private int port;
    private static final Bootstrap b;

    public NettyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // 初始化相关资源比如 EventLoopGroup、Bootstrap
    static {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        KryoSerializer kryoSerializer = new KryoSerializer();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
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

    /**
     * 发送消息到服务端
     *
     * @param rpcRequest 消息体
     * @return 服务端返回的数据
     */
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        try {
            ChannelFuture f = b.connect(host, port).sync();
            logger.info("client connect {}", host + ":" + port);
            Channel futureChannel = f.channel();
            if (futureChannel != null) {
                futureChannel.writeAndFlush(rpcRequest).addListener(future -> {
                    if (future.isSuccess()) {
                        logger.info(String.format("client send message: %s", rpcRequest.toString()));
                    } else {
                        logger.error("Send failed:", future.cause());
                    }
                });
                futureChannel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                RpcResponse rpcResponse = futureChannel.attr(key).get();
                // 校验 RpcResponse 和 RpcRequest
                RpcMessageChecker.check(rpcResponse, rpcRequest);
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            logger.error("occur com.axinstar.rpc.exception when connect server:", e);
        }
        return null;
    }
}
