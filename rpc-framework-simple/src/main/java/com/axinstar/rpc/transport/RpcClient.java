package com.axinstar.rpc.transport;

import com.axinstar.rpc.dto.RpcRequest;

/**
 * 实现了 RpcClient 接口的对象需要具有发送 RpcRequest 的能力
 *
 * @author axin
 * @since 2024/04/01
 */
public interface RpcClient {

    Object sendRpcRequest(RpcRequest rpcRequest);
}
