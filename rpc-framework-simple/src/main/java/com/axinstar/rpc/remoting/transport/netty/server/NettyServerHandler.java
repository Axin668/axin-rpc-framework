package com.axinstar.rpc.remoting.transport.netty.server;

import com.axinstar.rpc.enumeration.RpcMessageType;
import com.axinstar.rpc.remoting.dto.RpcRequest;
import com.axinstar.rpc.remoting.dto.RpcResponse;
import com.axinstar.rpc.remoting.handler.RpcRequestHandler;
import com.axinstar.rpc.factory.SingletonFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 自定义服务端的 ChannelHandler 来处理客户端发过来的数据
 * <p>
 * 如果继承自 SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放, {@link SimpleChannelInboundHandler} 内部的
 * channelRead 方法会替你释放 ByteBuf, 避免可能导致的内存泄露问题. 详见<<Netty进阶之路 跟着案例学 Netty>>
 *
 * @author axin
 * @since 2024/04/02
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.info("server receive msg: [{}]", msg);
            RpcRequest rpcRequest = (RpcRequest) msg;
            if (rpcRequest.getRpcMessageType() == RpcMessageType.HEART_BEAT) {
                log.info("receive heart beat msg from client");
                return;
            }
            // 执行目标方法（客户端需要执行的方法）并且返回方法结果
            Object result = rpcRequestHandler.handle(rpcRequest);
            log.info(String.format("server get result: %s", result.toString()));
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                // 返回方法执行结果给客户端
                RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                log.error("not writable now, message dropped");
            }
        } finally {
            //确保 ByteBuf 被释放，不然可能会有内存泄露问题
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
