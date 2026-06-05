package com.bhagwat.scm.customerService.command.service;

import com.bhagwat.scm.customerService.command.entity.Customer;
import com.bhagwat.scm.customerService.command.repository.JpaCustomerRepository;
import com.bhagwat.scm.core.rest.api.ApiClient;
import com.bhagwat.scm.core.rest.config.ServiceApiRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Forgot password flow:
 * 1. Customer enters email → magic link sent (valid 30 min)
 * 2. Customer clicks link → prompted for new password
 * 3. Password updated (BCrypt) → SMS notification sent to mobile
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final JpaCustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApiClient apiClient;
    private final ServiceApiRegistry apiRegistry;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    // Token store: token → {email, expiresAt}
    private final ConcurrentHashMap<String, ResetToken> tokenStore = new ConcurrentHashMap<>();

    record ResetToken(String email, Instant expiresAt) {}

    /**
     * Step 1: Generate magic link and "send" email.
     * In production, integrate with SendGrid/SES. For now, logs the link.
     */
    public Map<String, Object> requestPasswordReset(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found for email: " + email));

        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(1800); // 30 minutes
        tokenStore.put(token, new ResetToken(email, expiresAt));

        String magicLink = frontendUrl + "/reset-password?token=" + token;

        // TODO: Send email via SendGrid/SES
        log.info("═══════════════════════════════════════════════════════");
        log.info("MAGIC LINK for {}: {}", email, magicLink);
        log.info("Valid until: {}", expiresAt);
        log.info("═══════════════════════════════════════════════════════");

        return Map.of(
                "success", true,
                "message", "Password reset link sent to " + email,
                "expiresInMinutes", 30
        );
    }

    /**
     * Step 2: Validate token (called when user clicks magic link).
     */
    public Map<String, Object> validateResetToken(String token) {
        ResetToken rt = tokenStore.get(token);
        if (rt == null) throw new RuntimeException("Invalid or expired reset link");
        if (Instant.now().isAfter(rt.expiresAt())) {
            tokenStore.remove(token);
            throw new RuntimeException("Reset link has expired. Please request a new one.");
        }
        return Map.of("valid", true, "email", rt.email());
    }

    /**
     * Step 3: Reset password using the magic link token.
     * - Validates token
     * - Updates password (BCrypt)
     * - Sends SMS notification
     * - Invalidates token
     */
    @Transactional
    public Map<String, Object> resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match");
        }
        if (newPassword.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters");
        }

        ResetToken rt = tokenStore.get(token);
        if (rt == null) throw new RuntimeException("Invalid or expired reset link");
        if (Instant.now().isAfter(rt.expiresAt())) {
            tokenStore.remove(token);
            throw new RuntimeException("Reset link has expired");
        }

        Customer customer = customerRepository.findByEmail(rt.email())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Update password (BCrypt encrypted)
        customer.setPasswordHash(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);

        // Invalidate token (one-time use)
        tokenStore.remove(token);

        // Send SMS notification
        sendPasswordChangeSms(customer.getMobileNumber(), customer.getFname());

        log.info("Password reset successful for: {}", rt.email());

        return Map.of("success", true, "message", "Password updated successfully");
    }

    /**
     * Send SMS notification via notificationService.
     */
    private void sendPasswordChangeSms(String mobileNumber, String name) {
        if (mobileNumber == null || mobileNumber.isBlank()) return;
        String message = "Hi " + (name != null ? name : "Customer")
                + ", your Commart password was changed successfully. If this wasn't you, contact support immediately.";
        try {
            Map<String, Object> body = Map.of(
                    "channel", "SMS", "sender", "COMMART", "recipient", mobileNumber,
                    "subject", "Password Changed", "message", message);
            apiClient.invoke(apiRegistry.getConfig("notification-send"), body, Object.class);
            log.info("Password change SMS sent to {}", mobileNumber);
        } catch (Exception e) {
            log.warn("Failed to send SMS notification (non-fatal): {}", e.getMessage());
        }
    }
}
