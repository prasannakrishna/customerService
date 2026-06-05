package com.bhagwat.scm.customerService.command.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis-backed OTP, caching, and rate limiting for scalable customer auth.
 * Supports horizontal scaling (multiple pods share same Redis).
 */
@Service @RequiredArgsConstructor @Slf4j
public class RedisAuthService {

    private final StringRedisTemplate redis;

    private static final String OTP_PREFIX = "otp:";
    private static final String RATE_PREFIX = "rate:";
    private static final String SESSION_PREFIX = "session:";
    private static final String RESET_PREFIX = "reset:";

    // ── OTP Management ───────────────────────────────────────────────────

    /** Store OTP in Redis with 5-minute TTL */
    public String generateAndStoreOtp(String mobileNumber) {
        String otp = String.valueOf(100000 + new java.util.Random().nextInt(900000));
        redis.opsForValue().set(OTP_PREFIX + mobileNumber, otp, Duration.ofMinutes(5));
        log.info("OTP stored in Redis for {}", mobileNumber);
        return otp;
    }

    /** Verify OTP — returns true and deletes if valid */
    public boolean verifyOtp(String mobileNumber, String otp) {
        String stored = redis.opsForValue().get(OTP_PREFIX + mobileNumber);
        if (stored != null && stored.equals(otp)) {
            redis.delete(OTP_PREFIX + mobileNumber);
            return true;
        }
        return false;
    }

    // ── Rate Limiting ────────────────────────────────────────────────────

    /** Check if request is rate-limited. Returns true if BLOCKED. */
    public boolean isRateLimited(String key, int maxAttempts, int windowSeconds) {
        String redisKey = RATE_PREFIX + key;
        Long count = redis.opsForValue().increment(redisKey);
        if (count != null && count == 1) {
            redis.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
        }
        return count != null && count > maxAttempts;
    }

    /** Rate limit login attempts: 5 per minute per IP/username */
    public boolean isLoginRateLimited(String identifier) {
        return isRateLimited("login:" + identifier, 5, 60);
    }

    /** Rate limit OTP requests: 3 per 5 minutes per mobile */
    public boolean isOtpRateLimited(String mobile) {
        return isRateLimited("otp:" + mobile, 3, 300);
    }

    // ── Session/Profile Cache ────────────────────────────────────────────

    /** Cache user profile after login (avoids DB hit on subsequent requests) */
    public void cacheUserProfile(String userId, String profileJson) {
        redis.opsForValue().set(SESSION_PREFIX + userId, profileJson, Duration.ofHours(1));
    }

    /** Get cached profile (returns null if expired/missing) */
    public String getCachedProfile(String userId) {
        return redis.opsForValue().get(SESSION_PREFIX + userId);
    }

    /** Invalidate cache on profile update */
    public void invalidateCache(String userId) {
        redis.delete(SESSION_PREFIX + userId);
    }

    // ── Password Reset Tokens ────────────────────────────────────────────

    /** Store reset token with 30-minute TTL */
    public void storeResetToken(String token, String email) {
        redis.opsForValue().set(RESET_PREFIX + token, email, Duration.ofMinutes(30));
    }

    /** Validate and consume reset token */
    public String consumeResetToken(String token) {
        String email = redis.opsForValue().get(RESET_PREFIX + token);
        if (email != null) redis.delete(RESET_PREFIX + token);
        return email;
    }
}
