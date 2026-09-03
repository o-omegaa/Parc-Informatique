package com.supplierportal.infrastructure.security.ratelimit;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthenticationRateLimiter {

    private static final int MAX_ATTEMPTS_PER_MINUTE = 10;
    private static final long WINDOW_SECONDS = 60;
    private final ConcurrentHashMap<String, Deque<Instant>> attempts = new ConcurrentHashMap<>();

    public boolean allow(String key) {
        Instant now = Instant.now();
        Deque<Instant> timestamps = attempts.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (timestamps) {
            Instant limit = now.minusSeconds(WINDOW_SECONDS);
            while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(limit)) {
                timestamps.removeFirst();
            }
            if (timestamps.size() >= MAX_ATTEMPTS_PER_MINUTE) {
                return false;
            }
            timestamps.addLast(now);
            return true;
        }
    }
}
