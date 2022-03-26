package org.mryao.ws.util;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdUtil {

    public static final String KEY_PATTERN = "(0|([1-9][0-9]*)).[0-9A-Za-z_-]{24}";

    private static final SecureRandom numberGenerator = new SecureRandom();

    private static int index = -1;

    static {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            String[] strings = hostName.split("-");
            index = Integer.parseInt(strings[strings.length - 1]);
        } catch (Exception e) {
            log.error("getHostName error", e);
        }
    }

    public static String getRandomKey() {
        byte[] randomBytes = new byte[18];
        numberGenerator.nextBytes(randomBytes);
        return String.format("%d.%s", index, Base64.getUrlEncoder().encodeToString(randomBytes));
    }
}
