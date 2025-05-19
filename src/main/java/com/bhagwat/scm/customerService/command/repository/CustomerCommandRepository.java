package com.bhagwat.scm.customerService.command.repository;

import com.bhagwat.scm.customerService.command.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCommandRepository extends JpaRepository<Customer, Long> {

}
