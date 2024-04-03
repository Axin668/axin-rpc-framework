package com.axinstar.rpc.transport.netty.codec;

import com.axinstar.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 自定义解码器. 负责处理"入站"消息, 将消息格式转换为我们需要的业务对象
 *
 * @author axin
 * @since 2024/04/02
 */
@AllArgsConstructor
public class NettyKryoDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(NettyKryoDecoder.class);

    private Serializer serializer;
    private Class<?> genericClass;

    /**
     * Netty 传输的消息长度也就是对象序列化后对应的字节数组的大小, 存储在 ByteBuf 头部
     */
    private static final int BODY_LENGTH = 4;

    /**
     * 解码 ByteBuf 对象
     *
     * @param ctx 解码器关联的 ChannelHandlerContext 对象
     * @param in "入站"数据, 也就是 ByteBuf 对象
     * @param out 解码之后的数据对象需要添加到 out 对象里边
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1. byteBuf 中写入的消息长度所占的字节数已经是4了, 所以 byteBuf 的可读字节必须大于 4
        if (in.readableBytes() >= BODY_LENGTH) {
            // 2. 标记当前 readIndex 的位置, 以便后面重置 readIndex 的时候使用
            in.markReaderIndex();
            // 3. 读取消息的长度
            int dataLength = in.readInt();
            // 4. 遇到不合理的情况直接 return
            if (dataLength < 0 || in.readableBytes() < 0) {
                logger.error("data length or byteBuf readableBytes is not valid");
                return;
            }
            // 5. 如果可读字节数小于消息长度的话, 说明不是完整的消息, 重置readIndex
            if (in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }
            // 6. 走到这里说明没什么问题了, 可以序列化了
            byte[] body = new byte[dataLength];
            in.readBytes(body);
            // 将bytes数组转换为我们需要的对象
            Object obj = serializer.deserialize(body, genericClass);
            out.add(obj);
            logger.info("successful decode ByteBuf to Object");
        }
    }
}
