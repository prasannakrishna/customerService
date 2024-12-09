package com.bhagwat.scm.customerService.handler;

import com.bhagwat.scm.customerService.dto.CustomerCreatedEvent;
import com.bhagwat.scm.customerService.dto.CustomerDeletedEvent;
import com.bhagwat.scm.customerService.dto.CustomerUpdatedEvent;
import com.bhagwat.scm.customerService.entity.CustomerEntity;
import com.bhagwat.scm.customerService.repository.CustomerRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerQueryHandler {

    private final CustomerRepository customerRepository;

    public CustomerQueryHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @EventHandler
    public void on(CustomerCreatedEvent event) {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(event.getId());
        customer.setFname(event.getFname());
        customer.setMname(event.getMname());
        customer.setLname(event.getLname());
        customer.setEmail(event.getEmail());
        customer.setMobileNumber(event.getMobileNumber());
        customer.setAddress(event.getAddress());
        customerRepository.save(customer);
    }

    @EventHandler
    public void on(CustomerUpdatedEvent event) {
        Optional<CustomerEntity> customerOpt = customerRepository.findById(Long.valueOf(event.getId()));
        customerOpt.ifPresent(customer -> {
            customer.setFname(event.getFname());
            customer.setMname(event.getMname());
            customer.setLname(event.getLname());
            customer.setEmail(event.getEmail());
            customer.setMobileNumber(event.getMobileNumber());
            customer.setAddress(event.getAddress());
            customerRepository.save(customer);
        });
    }

    @EventHandler
    public void on(CustomerDeletedEvent event) {
        customerRepository.deleteById(Long.valueOf(event.getId()));
    }
}

