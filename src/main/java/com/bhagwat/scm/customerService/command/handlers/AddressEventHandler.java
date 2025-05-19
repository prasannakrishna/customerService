package com.bhagwat.scm.customerService.command.handlers;

import com.bhagwat.scm.customerService.command.entity.Address;
import com.bhagwat.scm.customerService.command.entity.Customer;
import com.bhagwat.scm.customerService.command.events.AddressCreatedEvent;
import com.bhagwat.scm.customerService.command.repository.AddressCommandRepository;
import com.bhagwat.scm.customerService.command.repository.CustomerCommandRepository;
import jakarta.persistence.EntityNotFoundException;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class AddressEventHandler {
    @Autowired
    private final AddressCommandRepository addressRepository;
    @Autowired
    private final CustomerCommandRepository customerCommandRepository;

    public AddressEventHandler(AddressCommandRepository addressRepository, CustomerCommandRepository customerCommandRepository) {
        this.addressRepository = addressRepository;
        this.customerCommandRepository = customerCommandRepository;
    }


    @EventHandler
    public void on(AddressCreatedEvent event) {
        Address address = new Address();
        address.setAddressId(Long.valueOf(event.getAddressId()));
        Customer customer = customerCommandRepository.findById(Long.valueOf(event.getCustomerId()))
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        address.setCustomer(customer);
        address.setCustomer(customer);
        address.setAddressLine1(event.getAddressLine1());
        address.setAddressLine2(event.getAddressLine2());
        address.setCity(event.getCity());
        address.setState(event.getState());
        address.setPincode(event.getPincode());
        address.setPrimaryAddress(event.isPrimaryAddress());
        addressRepository.save(address);
    }
}
