package com.bhagwat.scm.customerService.command.service;
import com.bhagwat.scm.customerService.command.entity.Address;
import com.bhagwat.scm.customerService.command.entity.Customer;
import com.bhagwat.scm.customerService.command.events.CustomerEvent;
import com.bhagwat.scm.customerService.command.repository.JpaCustomerRepository;
import com.bhagwat.scm.customerService.dto.AddressDto;
import com.bhagwat.scm.customerService.dto.CreateCustomerRequest;
import com.bhagwat.scm.customerService.dto.CustomerDto;
import com.bhagwat.scm.customerService.dto.UpdateCustomerRequest;
import jakarta.transaction.Transactional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerCommandService {

    private final JpaCustomerRepository customerRepository;
    private final KafkaTemplate<String, CustomerEvent> kafkaTemplate;
    private final PasswordEncoder passwordEncoder;

    public CustomerCommandService(JpaCustomerRepository customerRepository,
                                  KafkaTemplate<String, CustomerEvent> kafkaTemplate,
                                  PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CustomerDto createCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setUsername(request.getUsername());
        if (request.getPassword() != null) {
            customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        customer.setFname(request.getFname());
        customer.setMname(request.getMname());
        customer.setLname(request.getLname());
        customer.setEmail(request.getEmail());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setEmailVerified(false);
        customer.setMobileVerified(false);

        List<Address> addresses = request.getAddresses().stream()
                .map(dto -> {
                    Address address = mapAddressDtoToEntity(dto);
                    address.setCustomer(customer);
                    return address;
                })
                .collect(Collectors.toList());
        customer.setAddresses(addresses);
        Customer savedCustomer = customerRepository.save(customer);

        CustomerDto customerDto = mapCustomerToDto(savedCustomer);
        publishCustomerEvent(customerDto, "CUSTOMER_CREATED");

        return customerDto;
    }

    @Transactional
    public CustomerDto updateCustomer(UUID id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setFname(request.getFname());
        customer.setMname(request.getMname());
        customer.setLname(request.getLname());
        customer.setEmail(request.getEmail());
        customer.setMobileNumber(request.getMobileNumber());

        customer.getAddresses().clear();
        List<Address> addresses = request.getAddresses().stream()
                .map(dto -> {
                    Address address = mapAddressDtoToEntity(dto);
                    address.setCustomer(customer);
                    return address;
                })
                .collect(Collectors.toList());
        customer.setAddresses(addresses);

        Customer updatedCustomer = customerRepository.save(customer);

        CustomerDto customerDto = mapCustomerToDto(updatedCustomer);
        publishCustomerEvent(customerDto, "CUSTOMER_UPDATED");

        return customerDto;
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found");
        }
        Customer customer = customerRepository.findById(id).get();
        CustomerDto customerDto = mapCustomerToDto(customer);
        customerRepository.deleteById(id);
        publishCustomerEvent(customerDto, "CUSTOMER_DELETED");
    }

    private void publishCustomerEvent(CustomerDto dto, String eventType) {
        CustomerEvent event = new CustomerEvent(UUID.randomUUID(), dto.getId(), eventType, dto);
        kafkaTemplate.send("customer-events", dto.getId().toString(), event);
    }

    private CustomerDto mapCustomerToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setUsername(customer.getUsername());
        dto.setFname(customer.getFname());
        dto.setMname(customer.getMname());
        dto.setLname(customer.getLname());
        dto.setEmail(customer.getEmail());
        dto.setMobileNumber(customer.getMobileNumber());
        dto.setEmailVerified(customer.isEmailVerified());
        dto.setMobileVerified(customer.isMobileVerified());
        if (customer.getAddresses() != null) {
            List<AddressDto> addressDtos = customer.getAddresses().stream()
                    .map(this::mapAddressEntityToDto)
                    .collect(Collectors.toList());
            dto.setAddresses(addressDtos);
        }
        return dto;
    }

    private AddressDto mapAddressEntityToDto(Address address) {
        AddressDto dto = new AddressDto();
        dto.setAddressId(address.getAddressId());
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setCity(address.getCity());
        dto.setPost(address.getPost());
        dto.setPincode(address.getPincode());
        dto.setState(address.getState());
        dto.setCountry(address.getCountry());
        dto.setLandMark(address.getLandMark());
        dto.setLongitude(address.getLongitude());
        dto.setLatitude(address.getLatitude());
        dto.setPrimaryAddress(address.isPrimaryAddress());
        return dto;
    }

    private Address mapAddressDtoToEntity(AddressDto dto) {
        Address entity = new Address();
        entity.setAddressId(dto.getAddressId());
        entity.setAddressLine1(dto.getAddressLine1());
        entity.setAddressLine2(dto.getAddressLine2());
        entity.setCity(dto.getCity());
        entity.setPost(dto.getPost());
        entity.setPincode(dto.getPincode());
        entity.setState(dto.getState());
        entity.setCountry(dto.getCountry());
        entity.setLandMark(dto.getLandMark());
        entity.setLongitude(dto.getLongitude());
        entity.setLatitude(dto.getLatitude());
        entity.setPrimaryAddress(dto.isPrimaryAddress());
        return entity;
    }
}