package com.bhagwat.scm.customerService.command.service;

import com.bhagwat.scm.customerService.command.entity.Address;
import com.bhagwat.scm.customerService.command.entity.Customer;
import com.bhagwat.scm.customerService.command.events.CustomerEvent;
import com.bhagwat.scm.customerService.command.repository.JpaCustomerRepository;
import com.bhagwat.scm.customerService.dto.*;
import jakarta.transaction.Transactional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final JpaCustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, CustomerEvent> kafkaTemplate;

    public AuthService(JpaCustomerRepository customerRepository,
                       PasswordEncoder passwordEncoder,
                       KafkaTemplate<String, CustomerEvent> kafkaTemplate) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
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

        CustomerDto dto = toDto(saved);
        kafkaTemplate.send("customer-events", saved.getId().toString(),
                new CustomerEvent(UUID.randomUUID(), saved.getId(), "CUSTOMER_CREATED", dto));

        return new LoginResponse(saved.getId(), saved.getUsername(), saved.getFname(), saved.getLname(), saved.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
        Customer customer = customerRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }

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
