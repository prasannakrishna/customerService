package com.bhagwat.scm.customerService.command.service;

import com.bhagwat.scm.customerService.command.entity.Address;
import com.bhagwat.scm.customerService.command.entity.Customer;
import com.bhagwat.scm.customerService.command.events.CustomerEvent;
import com.bhagwat.scm.customerService.command.repository.JpaCustomerRepository;
import com.bhagwat.scm.customerService.dto.*;
import com.bhagwat.scm.core.rest.api.ApiClient;
import com.bhagwat.scm.core.rest.config.ServiceApiRegistry;
import com.bhagwat.scm.kafka.envelope.KafkaEventEnvelope;
import com.bhagwat.scm.kafka.producer.KafkaMessageProducer;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final JpaCustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final ApiClient apiClient;
    private final ServiceApiRegistry apiRegistry;

    public AuthService(JpaCustomerRepository customerRepository,
                       PasswordEncoder passwordEncoder,
                       KafkaMessageProducer kafkaMessageProducer,
                       ApiClient apiClient,
                       ServiceApiRegistry apiRegistry, RedisAuthService redisAuth) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaMessageProducer = kafkaMessageProducer;
        this.apiClient = apiClient;
        this.apiRegistry = apiRegistry;
        this.redisAuth = redisAuth;
    }

    @Transactional
    public LoginResponse register(CreateCustomerRequest request) {
        if (customerRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        Customer customer = new Customer();
        customer.setUsername(request.getUsername());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setFname(request.getFname());
        customer.setMname(request.getMname());
        customer.setLname(request.getLname());
        customer.setEmail(request.getEmail());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setEmailVerified(false);
        customer.setMobileVerified(false);

        if (request.getAddresses() != null) {
            List<Address> addresses = request.getAddresses().stream()
                    .map(dto -> {
                        Address address = new Address();
                        address.setAddressLine1(dto.getAddressLine1());
                        address.setAddressLine2(dto.getAddressLine2());
                        address.setCity(dto.getCity());
                        address.setPost(dto.getPost());
                        address.setPincode(dto.getPincode());
                        address.setState(dto.getState());
                        address.setCountry(dto.getCountry());
                        address.setLandMark(dto.getLandMark());
                        address.setLongitude(dto.getLongitude());
                        address.setLatitude(dto.getLatitude());
                        address.setPrimaryAddress(dto.isPrimaryAddress());
                        address.setCustomer(customer);
                        return address;
                    }).collect(Collectors.toList());
            customer.setAddresses(addresses);
        }

        Customer saved = customerRepository.save(customer);

        // Publish event (non-blocking — don't roll back registration if Kafka is down)
        try {
            CustomerDto dto = toDto(saved);
            kafkaMessageProducer.sendEnvelope("customer-events",
                    KafkaEventEnvelope.<CustomerEvent>builder()
                            .eventType("CUSTOMER_CREATED").source("customerService")
                            .payload(new CustomerEvent(UUID.randomUUID(), saved.getId(), "CUSTOMER_CREATED", dto)).build());
        } catch (Exception e) {
            System.out.println("Kafka publish failed (non-fatal): " + e.getMessage());
        }

        return new LoginResponse(saved.getId(), saved.getUsername(), saved.getFname(), saved.getLname(), saved.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
        // Rate limit: 5 login attempts per minute per username
        if (redisAuth.isLoginRateLimited(request.getUsername())) {
            throw new RuntimeException("Too many login attempts. Try again in 1 minute.");
        }

        Customer customer = customerRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }

        return new LoginResponse(customer.getId(), customer.getUsername(), customer.getFname(), customer.getLname(), customer.getEmail());
    }

    // ── OTP-based login (Redis-backed) ─────────────────────────────────

    private final RedisAuthService redisAuth;

    public void sendOtp(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.isBlank()) throw new RuntimeException("Mobile number required");

        // Rate limit: 3 OTP requests per 5 minutes
        if (redisAuth.isOtpRateLimited(mobileNumber)) {
            throw new RuntimeException("Too many OTP requests. Try again in 5 minutes.");
        }

        String otp = redisAuth.generateAndStoreOtp(mobileNumber);
        System.out.println("OTP for " + mobileNumber + ": " + otp);

        // Send OTP via notificationService
        try {
            java.util.Map<String, Object> body = java.util.Map.of(
                    "channel", "SMS", "sender", "COMMART", "recipient", mobileNumber,
                    "subject", "OTP Verification",
                    "message", "Your Commart OTP is: " + otp + ". Valid for 5 minutes. Do not share.");
            apiClient.invoke(apiRegistry.getConfig("notification-send"), body, Object.class);
        } catch (Exception e) {
            System.out.println("Notification service unavailable, OTP logged to console only: " + e.getMessage());
        }
    }

    public LoginResponse loginWithOtp(String mobileNumber, String otp) {
        if (!redisAuth.verifyOtp(mobileNumber, otp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("No account found for this mobile number"));

        customer.setMobileVerified(true);
        customerRepository.save(customer);

        return new LoginResponse(customer.getId(), customer.getUsername(), customer.getFname(), customer.getLname(), customer.getEmail());
    }

    private CustomerDto toDto(Customer c) {
        CustomerDto dto = new CustomerDto();
        dto.setId(c.getId());
        dto.setUsername(c.getUsername());
        dto.setFname(c.getFname());
        dto.setMname(c.getMname());
        dto.setLname(c.getLname());
        dto.setEmail(c.getEmail());
        dto.setMobileNumber(c.getMobileNumber());
        dto.setEmailVerified(c.isEmailVerified());
        dto.setMobileVerified(c.isMobileVerified());
        return dto;
    }
}
