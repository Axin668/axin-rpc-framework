package com.axinstar.rpc.transport.netty.client;

import com.axinstar.rpc.dto.RpcResponse;
import com.axinstar.rpc.transport.netty.codec.NettyKryoDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义客户端 ChannelHandler 来处理服务端发送过来的数据
 *
 * <p>
 * 如果继承自 SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放, {@link io.netty.channel.SimpleChannelInboundHandler} 内部的
 * channelRead 方法会替你释放 ByteBuf, 避免可能导致的内存泄露问题。详见<<Netty进阶之路 跟着案例学 Netty>>
 *
 * @author axin
 * @since 2024/04/02
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyKryoDecoder.class);

    /**
     * 读取服务端传输的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            logger.info(String.format("client receive msg: %s", msg));
            RpcResponse rpcResponse = (RpcResponse) msg;
            // 声明一个 AttributeKey 对象, 类似于 Map 中的 Key
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcResponse.getRequestId());
            /**
             * AttributeMap 可以看作是一个Channel的共享数据源
             * AttributeMap的key是AttributeKey, value是Attribute
             * 将服务端的返回结果保存到 AttributeMap 上
             */
            ctx.channel().attr(key).set(rpcResponse);
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 处理客户端消息发生异常的时候被调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client catch exception:", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
