package com.axinstar.rpc.remoting.transport.netty.client;

import com.axinstar.rpc.factory.SingletonFactory;
import com.axinstar.rpc.remoting.dto.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 自定义客户端 ChannelHandler 来处理服务端发送过来的数据
 *
 * <p>
 * 如果继承自 SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放, {@link SimpleChannelInboundHandler} 内部的
 * channelRead 方法会替你释放 ByteBuf, 避免可能导致的内存泄露问题。详见<<Netty进阶之路 跟着案例学 Netty>>
 *
 * @author axin
 * @since 2024/04/02
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }


    /**
     * 读取服务端传输的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.info("client receive msg: [{}]", msg);
            RpcResponse rpcResponse = (RpcResponse) msg;
            unprocessedRequests.complete(rpcResponse);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 处理客户端消息发生异常的时候被调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client catch exception:", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
