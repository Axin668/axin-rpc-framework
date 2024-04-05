package com.axinstar.rpc.remoting.transport;

import com.axinstar.rpc.remoting.dto.RpcRequest;

/**
 * 传输 RpcRequest
 *
 * @author axin
 * @since 2024/04/04
 */
public interface ClientTransport {

    /**
     * 发送消息到服务端
     *
     * @param rpcRequest 消息体
     * @return 服务端返回的数据
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
