package com.axinstar.rpc.dto;

import com.axinstar.rpc.enumeration.RpcMessageType;
import lombok.*;

import java.io.Serializable;

/**
 * @author axin
 * @since 2024/03/30
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private RpcMessageType rpcMessageTypeEnum;
}
