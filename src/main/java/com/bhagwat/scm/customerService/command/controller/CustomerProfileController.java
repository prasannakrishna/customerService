package com.bhagwat.scm.customerService.command.controller;

import com.bhagwat.scm.customerService.command.entity.Address;
import com.bhagwat.scm.customerService.command.entity.Customer;
import com.bhagwat.scm.customerService.command.repository.JpaCustomerRepository;
import com.bhagwat.scm.customerService.dto.AddressDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final JpaCustomerRepository customerRepository;

    // ── Profile ─────────────────────────────────────────────────────────

    /** Get customer profile */
    @GetMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable UUID customerId) {
        Customer c = findCustomer(customerId);
        return ResponseEntity.ok(toProfileMap(c));
    }

    /** Update customer profile fields */
    @PatchMapping("/{customerId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable UUID customerId,
            @RequestBody Map<String, Object> updates) {
        Customer c = findCustomer(customerId);
        if (updates.containsKey("fname")) c.setFname((String) updates.get("fname"));
        if (updates.containsKey("mname")) c.setMname((String) updates.get("mname"));
        if (updates.containsKey("lname")) c.setLname((String) updates.get("lname"));
        if (updates.containsKey("email")) c.setEmail((String) updates.get("email"));
        if (updates.containsKey("mobileNumber")) c.setMobileNumber((String) updates.get("mobileNumber"));
        if (updates.containsKey("profileImageUrl")) c.setProfileImageUrl((String) updates.get("profileImageUrl"));
        if (updates.containsKey("gender")) c.setGender((String) updates.get("gender"));
        if (updates.containsKey("preferredLanguage")) c.setPreferredLanguage((String) updates.get("preferredLanguage"));
        if (updates.containsKey("defaultLatitude")) c.setDefaultLatitude(toDouble(updates.get("defaultLatitude")));
        if (updates.containsKey("defaultLongitude")) c.setDefaultLongitude(toDouble(updates.get("defaultLongitude")));
        customerRepository.save(c);
        return ResponseEntity.ok(toProfileMap(c));
    }

    // ── Addresses ───────────────────────────────────────────────────────

    /** List all addresses for a customer */
    @GetMapping("/{customerId}/addresses")
    public ResponseEntity<List<AddressDto>> getAddresses(@PathVariable UUID customerId) {
        Customer c = findCustomer(customerId);
        List<AddressDto> dtos = c.getAddresses().stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /** Add a new address. If isPrimaryAddress=true, demotes the old primary. */
    @PostMapping("/{customerId}/addresses")
    @Transactional
    public ResponseEntity<AddressDto> addAddress(@PathVariable UUID customerId, @RequestBody AddressDto dto) {
        Customer c = findCustomer(customerId);

        if (dto.isPrimaryAddress()) {
            c.getAddresses().forEach(a -> a.setPrimaryAddress(false));
        } else if (c.getAddresses().isEmpty()) {
            dto.setPrimaryAddress(true); // first address is always primary
        }

        Address addr = toEntity(dto);
        addr.setCustomer(c);
        c.getAddresses().add(addr);
        customerRepository.save(c);

        Address saved = c.getAddresses().get(c.getAddresses().size() - 1);
        log.info("Address added for customer {}: {}, primary={}", customerId, saved.getAddressId(), saved.isPrimaryAddress());
        return ResponseEntity.ok(toDto(saved));
    }

    /** Update an existing address */
    @PutMapping("/{customerId}/addresses/{addressId}")
    @Transactional
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable UUID customerId,
            @PathVariable UUID addressId,
            @RequestBody AddressDto dto) {
        Customer c = findCustomer(customerId);
        Address addr = c.getAddresses().stream()
                .filter(a -> a.getAddressId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not found: " + addressId));

        if (dto.isPrimaryAddress() && !addr.isPrimaryAddress()) {
            c.getAddresses().forEach(a -> a.setPrimaryAddress(false));
        }

        addr.setAddressLine1(dto.getAddressLine1());
        addr.setAddressLine2(dto.getAddressLine2());
        addr.setCity(dto.getCity());
        addr.setPost(dto.getPost());
        addr.setPincode(dto.getPincode());
        addr.setState(dto.getState());
        addr.setCountry(dto.getCountry());
        addr.setLandMark(dto.getLandMark());
        addr.setLongitude(dto.getLongitude());
        addr.setLatitude(dto.getLatitude());
        addr.setPrimaryAddress(dto.isPrimaryAddress());
        addr.setAddressType(dto.getAddressType());
        addr.setAddressLabel(dto.getAddressLabel());
        addr.setContactPhone(dto.getContactPhone());
        addr.setPlaceId(dto.getPlaceId());
        addr.setPlusCode(dto.getPlusCode());
        addr.setFormattedAddress(dto.getFormattedAddress());

        customerRepository.save(c);
        return ResponseEntity.ok(toDto(addr));
    }

    /** Delete an address. Cannot delete the primary unless another exists. */
    @DeleteMapping("/{customerId}/addresses/{addressId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteAddress(
            @PathVariable UUID customerId,
            @PathVariable UUID addressId) {
        Customer c = findCustomer(customerId);
        Address addr = c.getAddresses().stream()
                .filter(a -> a.getAddressId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not found: " + addressId));

        boolean wasPrimary = addr.isPrimaryAddress();
        c.getAddresses().remove(addr);

        // If deleted address was primary and others exist, promote first remaining
        if (wasPrimary && !c.getAddresses().isEmpty()) {
            c.getAddresses().get(0).setPrimaryAddress(true);
        }

        customerRepository.save(c);
        return ResponseEntity.ok(Map.of("deleted", addressId, "remainingAddresses", c.getAddresses().size()));
    }

    /** Set an address as primary (demotes the old primary) */
    @PostMapping("/{customerId}/addresses/{addressId}/set-primary")
    @Transactional
    public ResponseEntity<AddressDto> setPrimary(
            @PathVariable UUID customerId,
            @PathVariable UUID addressId) {
        Customer c = findCustomer(customerId);
        c.getAddresses().forEach(a -> a.setPrimaryAddress(a.getAddressId().equals(addressId)));
        customerRepository.save(c);
        Address primary = c.getAddresses().stream()
                .filter(Address::isPrimaryAddress).findFirst()
                .orElseThrow(() -> new RuntimeException("Primary address not set"));
        return ResponseEntity.ok(toDto(primary));
    }

    /** Get only the primary address */
    @GetMapping("/{customerId}/addresses/primary")
    public ResponseEntity<AddressDto> getPrimaryAddress(@PathVariable UUID customerId) {
        Customer c = findCustomer(customerId);
        Address primary = c.getAddresses().stream()
                .filter(Address::isPrimaryAddress).findFirst()
                .orElseThrow(() -> new RuntimeException("No primary address found"));
        return ResponseEntity.ok(toDto(primary));
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private Customer findCustomer(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
    }

    private Map<String, Object> toProfileMap(Customer c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("username", c.getUsername());
        m.put("fname", c.getFname());
        m.put("mname", c.getMname());
        m.put("lname", c.getLname());
        m.put("email", c.getEmail());
        m.put("mobileNumber", c.getMobileNumber());
        m.put("emailVerified", c.isEmailVerified());
        m.put("mobileVerified", c.isMobileVerified());
        m.put("profileImageUrl", c.getProfileImageUrl());
        m.put("dateOfBirth", c.getDateOfBirth());
        m.put("gender", c.getGender());
        m.put("preferredLanguage", c.getPreferredLanguage());
        m.put("defaultLatitude", c.getDefaultLatitude());
        m.put("defaultLongitude", c.getDefaultLongitude());
        m.put("addressCount", c.getAddresses() != null ? c.getAddresses().size() : 0);
        return m;
    }

    private AddressDto toDto(Address a) {
        AddressDto dto = new AddressDto();
        dto.setAddressId(a.getAddressId());
        dto.setAddressLine1(a.getAddressLine1());
        dto.setAddressLine2(a.getAddressLine2());
        dto.setCity(a.getCity());
        dto.setPost(a.getPost());
        dto.setPincode(a.getPincode());
        dto.setState(a.getState());
        dto.setCountry(a.getCountry());
        dto.setLandMark(a.getLandMark());
        dto.setLongitude(a.getLongitude());
        dto.setLatitude(a.getLatitude());
        dto.setPrimaryAddress(a.isPrimaryAddress());
        dto.setAddressType(a.getAddressType());
        dto.setAddressLabel(a.getAddressLabel());
        dto.setContactPhone(a.getContactPhone());
        dto.setPlaceId(a.getPlaceId());
        dto.setPlusCode(a.getPlusCode());
        dto.setFormattedAddress(a.getFormattedAddress());
        return dto;
    }

    private Address toEntity(AddressDto dto) {
        Address a = new Address();
        a.setAddressLine1(dto.getAddressLine1());
        a.setAddressLine2(dto.getAddressLine2());
        a.setCity(dto.getCity());
        a.setPost(dto.getPost());
        a.setPincode(dto.getPincode());
        a.setState(dto.getState());
        a.setCountry(dto.getCountry());
        a.setLandMark(dto.getLandMark());
        a.setLongitude(dto.getLongitude());
        a.setLatitude(dto.getLatitude());
        a.setPrimaryAddress(dto.isPrimaryAddress());
        a.setAddressType(dto.getAddressType());
        a.setAddressLabel(dto.getAddressLabel());
        a.setContactPhone(dto.getContactPhone());
        a.setPlaceId(dto.getPlaceId());
        a.setPlusCode(dto.getPlusCode());
        a.setFormattedAddress(dto.getFormattedAddress());
        return a;
    }

    private Double toDouble(Object val) {
        if (val instanceof Number) return ((Number) val).doubleValue();
        return null;
    }
}
