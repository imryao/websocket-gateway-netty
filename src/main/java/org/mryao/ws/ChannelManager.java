package org.mryao.ws;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {

    public static final String KEY_PATTERN = "[0-9A-Za-z_-]{24}";

    private static final Map<String, Channel> channels = new ConcurrentHashMap<>();

    private static final AttributeKey<String> attributeKey = AttributeKey.newInstance("key");

    public static Channel put(String key, Channel channel) {
        channel.attr(attributeKey).set(key);
        return channels.put(key, channel);
    }

    public static Channel get(String key) {
        return channels.get(key);
    }

    public static String getByValue(Channel channel) {
        if (channel.hasAttr(attributeKey)) {
            return channel.attr(attributeKey).get();
        }
        return null;
    }

    @Deprecated
    public static Channel remove(String key) {
        return channels.remove(key);
    }

    public static String removeByValue(Channel channel) {
        String key = getByValue(channel);
        if (key != null) {
            channel = channels.remove(key);
            if (channel != null) {
                return key;
            }
        }
        return null;
    }
}
