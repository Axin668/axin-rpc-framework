package com.axinstar.rpc.loadbalance;

import java.util.List;

/**
 * @author axin
 * @since 2024/04/13
 */
public interface LoadBalance {

    /**
     * 在已有服务提供地址列表中选择一个
     *
     * @param serviceAddresses    服务地址列表
     * @return                  目标服务地址
     */
    String selectServiceAddress(List<String> serviceAddresses);
}
