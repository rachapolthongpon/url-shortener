package com.rthongpon.url_shortener.util;

import java.security.SecureRandom;

public class Base62 {
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final long MIN = (long) Math.pow(62, 6);  // 62⁶
    private static final long MAX = (long) Math.pow(62, 7);  // 62⁷
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String encodeBase62(long number) {
        StringBuilder sb = new StringBuilder();
        while(number > 0) {
            sb.append(CHARACTERS.charAt((int) (number % CHARACTERS.length())));
            number /= CHARACTERS.length();
        }
        return sb.reverse().toString();
    }

    public static String randomCode() {
        long number = RANDOM.nextLong(MIN, MAX);
        return encodeBase62(number);
    }
}
