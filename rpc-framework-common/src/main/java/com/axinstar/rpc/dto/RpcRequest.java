package com.axinstar.rpc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author axin
 * @since 2024/03/30
 */
@Data
@Builder
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
}
