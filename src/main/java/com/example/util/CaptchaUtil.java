package com.example.util;

import java.security.SecureRandom;

public class CaptchaUtil {
    private static final String CHARACTERS = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final int CAPTCHA_LENGTH = 4;
    private static final SecureRandom random = new SecureRandom();

    public static String generateCaptcha() {
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captcha.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return captcha.toString();
    }
}