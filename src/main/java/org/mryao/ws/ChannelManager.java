package org.mryao.ws;

import io.netty.channel.Channel;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {

    public static final String KEY_PATTERN = "[0-9A-Za-z_-]{24}";

    private static final Map<String, Channel> channels = new ConcurrentHashMap<>();

    public static Channel put(String key, Channel channel) {
        return channels.put(key, channel);
    }

    public static Channel get(String key) {
        return channels.get(key);
    }

    public static Channel remove(String key) {
        return channels.remove(key);
    }

    public static String removeByValue(Channel channel) {
        String id = channel.id().asLongText();
        Entry<String, Channel> entry = channels.entrySet().stream()
                .filter(e -> e.getValue().id().asLongText().equals(id)).findFirst().orElse(null);
        if (entry == null) {
            return null;
        }
        String key = entry.getKey();
        channels.remove(key);
        return key;
    }
}
