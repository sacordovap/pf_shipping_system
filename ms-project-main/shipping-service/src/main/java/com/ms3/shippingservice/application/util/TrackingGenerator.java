package com.ms3.shippingservice.application.util;

import java.security.SecureRandom;
public class TrackingGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateUniqueTracking() {
        long timestamp = System.currentTimeMillis();
        long randomPart = random.nextLong(1000000);
        String timePart = Long.toString(timestamp, 36).toUpperCase();
        String randPart = Long.toString(randomPart, 36).toUpperCase();

        return "TRK-" + timePart + "-" + randPart;
    }
}