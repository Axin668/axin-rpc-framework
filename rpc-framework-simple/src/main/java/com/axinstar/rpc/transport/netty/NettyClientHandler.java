package com.axinstar.rpc.transport.netty;

import com.axinstar.rpc.dto.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author axin
 * @since 2024/04/02
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyKryoDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            RpcResponse rpcResponse = (RpcResponse) msg;
            logger.info(String.format("client receive msg: %s", rpcResponse));
            // 声明一个 AttributeKey 对象
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            // 将服务端的返回结果保存到 AttributeMap 上, AttributeMap 可以看作是一个Channel的共享数据源
            // AttributeMap的key是AttributeKey, value是Attribute
            ctx.channel().attr(key).set(rpcResponse);
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
