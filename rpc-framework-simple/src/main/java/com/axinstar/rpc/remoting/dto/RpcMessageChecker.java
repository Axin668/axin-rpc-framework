package com.axinstar.rpc.remoting.dto;

import com.axinstar.rpc.enumeration.RpcErrorMessage;
import com.axinstar.rpc.enumeration.RpcResponseCode;
import com.axinstar.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

/**
 * 校验 RpcRequest 和 RpcResponse
 *
 * @author axin
 * @since 2024/04/03
 */
@Slf4j
public final class RpcMessageChecker {

    private static final String INTERFACE_NAME = "interfaceName";

    private RpcMessageChecker() {
    }

    public static void check(RpcResponse rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessage.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessage.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCode.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessage.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
