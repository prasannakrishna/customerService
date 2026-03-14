package com.bhagwat.scm.customerService.query.repository;
import com.bhagwat.scm.customerService.query.entity.CustomerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.UUID;

public interface MongoCustomerRepository extends MongoRepository<CustomerDocument, String> {
    Optional<CustomerDocument> findByEmail(String email);
    Optional<CustomerDocument> findByUsername(String username);
}