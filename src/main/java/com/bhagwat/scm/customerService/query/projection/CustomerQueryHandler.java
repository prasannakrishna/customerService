package com.bhagwat.scm.customerService.query.projection;

import com.bhagwat.scm.customerService.command.entity.Address;
import com.bhagwat.scm.customerService.command.events.CustomerCreatedEvent;
import com.bhagwat.scm.customerService.command.events.CustomerDeletedEvent;
import com.bhagwat.scm.customerService.command.events.CustomerUpdatedEvent;
import com.bhagwat.scm.customerService.command.entity.Customer;
import com.bhagwat.scm.customerService.command.repository.CustomerCommandRepository;
import com.bhagwat.scm.customerService.query.dto.FindCustomerById;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerQueryHandler {

    @Autowired
    private  CustomerCommandRepository customerCommandRepository;

    public CustomerQueryHandler(CustomerCommandRepository customerCommandRepository) {
        this.customerCommandRepository = customerCommandRepository;
    }

    @EventHandler
    public void on(CustomerCreatedEvent event) {
        Customer customer = new Customer();
        customer.setId(Long.valueOf(event.getId()));
        customer.setFname(event.getFname());
        customer.setMname(event.getMname());
        customer.setLname(event.getLname());
        customer.setEmail(event.getEmail());
        customer.setMobileNumber(event.getMobileNumber());
        List<Address> addressList = event.getAddresses().stream()
                .map(dto -> {
                    Address address = new Address();
                    address.setAddressLine1(dto.getAddressLine1());
                    address.setAddressLine2(dto.getAddressLine2());
                    address.setCity(dto.getCity());
                    address.setPost(dto.getPost());
                    address.setPincode(dto.getPincode());
                    address.setState(dto.getState());
                    address.setCountry(dto.getCountry());
                    address.setLandMark(dto.getLandMark());
                    address.setLongitude(dto.getLongitude());
                    address.setLatitude(dto.getLatitude());
                    address.setPrimaryAddress(dto.isPrimaryAddress());
                    return address;
                })
                .collect(Collectors.toList());
        customer.setAddresses(addressList);
        customerCommandRepository.save(customer);
    }

    @EventHandler
    public void on(CustomerUpdatedEvent event) {
        Optional<Customer> customerOpt = customerCommandRepository.findById(Long.valueOf(event.getId()));
        customerOpt.ifPresent(customer -> {
            customer.setFname(event.getFname());
            customer.setMname(event.getMname());
            customer.setLname(event.getLname());
            customer.setEmail(event.getEmail());
            customer.setMobileNumber(event.getMobileNumber());
            //customer.setAddress(event.getAddress());
            customerCommandRepository.save(customer);
        });
    }

    @EventHandler
    public void on(CustomerDeletedEvent event) {
        customerCommandRepository.deleteById(Long.valueOf(event.getId()));
    }

    @QueryHandler
    public Optional<Customer> handle(FindCustomerById query) {
        return customerCommandRepository.findById(query.getId());
    }
}

