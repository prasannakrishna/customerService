package com.bhagwat.scm.customerService.query.service;
import com.bhagwat.scm.customerService.dto.AddressDto;
import com.bhagwat.scm.customerService.dto.CustomerDto;
import com.bhagwat.scm.customerService.query.entity.CustomerDocument;
import com.bhagwat.scm.customerService.query.repository.MongoCustomerRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerQueryService {

    private final MongoCustomerRepository mongoCustomerRepository;

    public CustomerQueryService(MongoCustomerRepository mongoCustomerRepository) {
        this.mongoCustomerRepository = mongoCustomerRepository;
    }

    public List<CustomerDto> getAllCustomers() {
        return mongoCustomerRepository.findAll().stream()
                .map(this::mapDocumentToDto)
                .collect(Collectors.toList());
    }

    public CustomerDto getCustomerById(UUID id) {
        return mongoCustomerRepository.findById(String.valueOf(id))
                .map(this::mapDocumentToDto)
                .orElse(null);
    }

    public CustomerDto getCustomerByUsername(String username) {
        return mongoCustomerRepository.findByUsername(username)
                .map(this::mapDocumentToDto)
                .orElse(null);
    }

    private CustomerDto mapDocumentToDto(CustomerDocument document) {
        CustomerDto dto = new CustomerDto();
        dto.setId(UUID.fromString(document.getId()));
        dto.setUsername(document.getUsername());
        dto.setFname(document.getFname());
        dto.setMname(document.getMname());
        dto.setLname(document.getLname());
        dto.setEmail(document.getEmail());
        dto.setMobileNumber(document.getMobileNumber());
        dto.setEmailVerified(document.isEmailVerified());
        dto.setMobileVerified(document.isMobileVerified());
        if (document.getAddresses() != null) {
            List<AddressDto> addressDtos = document.getAddresses().stream()
                    .map(this::mapEmbeddedAddressToDto)
                    .collect(Collectors.toList());
            dto.setAddresses(addressDtos);
        }
        return dto;
    }

    private AddressDto mapEmbeddedAddressToDto(CustomerDocument.EmbeddedAddress embeddedAddress) {
        AddressDto dto = new AddressDto();
        dto.setAddressId(UUID.fromString(embeddedAddress.getAddressId()));
        dto.setAddressLine1(embeddedAddress.getAddressLine1());
        dto.setAddressLine2(embeddedAddress.getAddressLine2());
        dto.setCity(embeddedAddress.getCity());
        dto.setPost(embeddedAddress.getPost());
        dto.setPincode(embeddedAddress.getPincode());
        dto.setState(embeddedAddress.getState());
        dto.setCountry(embeddedAddress.getCountry());
        dto.setLandMark(embeddedAddress.getLandMark());
        dto.setLongitude(embeddedAddress.getLongitude());
        dto.setLatitude(embeddedAddress.getLatitude());
        dto.setPrimaryAddress(embeddedAddress.isPrimaryAddress());
        return dto;
    }
}