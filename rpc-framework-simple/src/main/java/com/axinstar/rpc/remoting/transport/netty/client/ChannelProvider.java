package com.axinstar.rpc.remoting.transport.netty.client;

import com.axinstar.rpc.factory.SingletonFactory;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * store and get Channel object
 *
 * @author axin
 * @since 2024/04/04
 */
@Slf4j
public final class ChannelProvider {

    private static final Map<String, Channel> channels = new ConcurrentHashMap<>();
    private static final NettyClient nettyClient = SingletonFactory.getInstance(NettyClient.class);

    private ChannelProvider() {
    }

    public static Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        // determine if there is a connection for the corresponding address
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            // if so, determine if the connection is available, and if so, get it directly
            if (channel != null && channel.isActive()) {
                return channel;
            }
            channels.remove(key);
        }
        // otherwise, reconnect to get the channel
        Channel channel = nettyClient.doConnect(inetSocketAddress);
        channels.put(key, channel);
        return channel;
    }

    public static void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channels.remove(key);
        log.info("Channel map size: [{}]", channels.size());
    }
}
