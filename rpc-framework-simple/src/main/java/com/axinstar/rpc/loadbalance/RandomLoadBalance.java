package com.axinstar.rpc.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * @author axin
 * @since 2024/04/13
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect(List<String> serviceAddresses) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
