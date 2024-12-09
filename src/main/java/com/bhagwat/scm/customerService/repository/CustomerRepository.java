package com.bhagwat.scm.customerService.repository;

import com.bhagwat.scm.customerService.entity.Customer;
import com.bhagwat.scm.customerService.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
