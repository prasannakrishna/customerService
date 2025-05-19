package com.bhagwat.scm.customerService.command.handlers;

import com.bhagwat.scm.customerService.command.entity.Customer;
import com.bhagwat.scm.customerService.command.events.CustomerCreatedEvent;
import com.bhagwat.scm.customerService.command.repository.CustomerCommandRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventHandler {
    @Autowired
    private  CustomerCommandRepository customerCommandRepository;

    @EventHandler
    public void on(CustomerCreatedEvent event) {
        Customer customer = new Customer();
        //customer.setId(Long.valueOf(event.getId()));
        customer.setFname(event.getFname());
        customer.setEmail(event.getEmail());
        customerCommandRepository.save(customer);
    }
}
