package com.bhagwat.scm.customerService.query.service;
import com.bhagwat.scm.customerService.command.events.CustomerEvent;
import com.bhagwat.scm.customerService.dto.AddressDto;
import com.bhagwat.scm.customerService.dto.CustomerDto;
import com.bhagwat.scm.customerService.query.entity.CustomerDocument;
import com.bhagwat.scm.customerService.query.repository.MongoCustomerRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KafkaConsumerService {

    private final MongoCustomerRepository mongoCustomerRepository;

    public KafkaConsumerService(MongoCustomerRepository mongoCustomerRepository) {
        this.mongoCustomerRepository = mongoCustomerRepository;
    }

    @KafkaListener(topics = "customer-events", groupId = "mongo-sync-group")
    public void listen(CustomerEvent event) {
        CustomerDto payload = event.getCustomerPayload();
        CustomerDocument document = new CustomerDocument();
        document.setId(String.valueOf(payload.getId()));
        document.setUsername(payload.getUsername());
        document.setFname(payload.getFname());
        document.setMname(payload.getMname());
        document.setLname(payload.getLname());
        document.setEmail(payload.getEmail());
        document.setMobileNumber(payload.getMobileNumber());
        document.setEmailVerified(payload.isEmailVerified());
        document.setMobileVerified(payload.isMobileVerified());

        List<CustomerDocument.EmbeddedAddress> embeddedAddresses = payload.getAddresses().stream()
                .map(this::mapAddressDtoToEmbeddedAddress)
                .collect(Collectors.toList());
        document.setAddresses(embeddedAddresses);

        switch (event.getEventType()) {
            case "CUSTOMER_CREATED":
            case "CUSTOMER_UPDATED":
                mongoCustomerRepository.save(document);
                break;
            case "CUSTOMER_DELETED":
                mongoCustomerRepository.deleteById(String.valueOf(UUID.fromString(document.getId())));
                break;
            default:
                System.out.println("Unknown event type: " + event.getEventType());
        }
    }

    private CustomerDocument.EmbeddedAddress mapAddressDtoToEmbeddedAddress(AddressDto dto) {
        CustomerDocument.EmbeddedAddress embeddedAddress = new CustomerDocument.EmbeddedAddress();
        embeddedAddress.setAddressId(String.valueOf(dto.getAddressId()));
        embeddedAddress.setAddressLine1(dto.getAddressLine1());
        embeddedAddress.setAddressLine2(dto.getAddressLine2());
        embeddedAddress.setCity(dto.getCity());
        embeddedAddress.setPost(dto.getPost());
        embeddedAddress.setPincode(dto.getPincode());
        embeddedAddress.setState(dto.getState());
        embeddedAddress.setCountry(dto.getCountry());
        embeddedAddress.setLandMark(dto.getLandMark());
        embeddedAddress.setLongitude(dto.getLongitude());
        embeddedAddress.setLatitude(dto.getLatitude());
        embeddedAddress.setPrimaryAddress(dto.isPrimaryAddress());
        return embeddedAddress;
    }
}