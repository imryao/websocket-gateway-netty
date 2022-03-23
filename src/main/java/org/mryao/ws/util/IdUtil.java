package org.mryao.ws.util;

import java.security.SecureRandom;
import java.util.Base64;

public class IdUtil {

    private static final SecureRandom numberGenerator = new SecureRandom();

    public static String getRandomString() {
        byte[] randomBytes = new byte[18];
        numberGenerator.nextBytes(randomBytes);
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }
}
