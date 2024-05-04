package com.axinstar.rpc.exception;

import com.axinstar.rpc.enumeration.RpcErrorMessage;

/**
 * @author axin
 * @since 2024/03/30
 */
public class RpcException extends RuntimeException {

    public RpcException(RpcErrorMessage rpcErrorMessage, String detail) {
        super(rpcErrorMessage.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessage rpcErrorMessage) {
        super(rpcErrorMessage.getMessage());
    }
}
