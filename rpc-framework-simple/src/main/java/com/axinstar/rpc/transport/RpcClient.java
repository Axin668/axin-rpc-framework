package com.axinstar.rpc.transport;

import com.axinstar.rpc.dto.RpcRequest;

/**
 * @author axin
 * @since 2024/04/01
 */
public interface RpcClient {

    Object sendRpcRequest(RpcRequest rpcRequest);
}
