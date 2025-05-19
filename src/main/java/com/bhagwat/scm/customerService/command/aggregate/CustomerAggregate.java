package com.bhagwat.scm.customerService.command.aggregate;

import com.bhagwat.scm.customerService.command.commanddto.CreateCustomerCommand;
import com.bhagwat.scm.customerService.command.commanddto.CreateOrderCommand;
import com.bhagwat.scm.customerService.command.entity.Address;
import com.bhagwat.scm.customerService.command.events.CustomerCreatedEvent;
import com.bhagwat.scm.customerService.command.events.CustomerDeletedEvent;
import com.bhagwat.scm.customerService.command.events.CustomerUpdatedEvent;
import com.bhagwat.scm.customerService.dto.AddressDto;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.List;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class CustomerAggregate {
    @AggregateIdentifier
    private String id;
    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String mobileNumber;
    private List<Address> address;

    public CustomerAggregate() {}

    @CommandHandler
    public CustomerAggregate(CreateCustomerCommand command) {
        apply(new CustomerCreatedEvent(command.getId(), command.getFname(), command.getMname(),
                command.getLname(), command.getEmail(),  command.getMobileNumber(),command.isEmailVerified(), command.isMobileVerified(), command.getAddresses()));
    }

    @EventSourcingHandler
    public void on(CustomerCreatedEvent event) {
        this.id = event.getId();
        this.fname = event.getFname();
        this.mname = event.getMname();
        this.lname = event.getLname();
        this.email = event.getEmail();
        this.mobileNumber = event.getMobileNumber();
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
        this.address = addressList;
    }

    @CommandHandler
    public void handle(CreateOrderCommand.UpdateCustomerCommand command) {
        apply(new CustomerUpdatedEvent(command.getId(), command.getFname(), command.getMname(),
                command.getLname(), command.getEmail(), command.getMobileNumber(), command.getAddress()));
    }

    @EventSourcingHandler
    public void on(CustomerUpdatedEvent event) {
        this.fname = event.getFname();
        this.mname = event.getMname();
        this.lname = event.getLname();
        this.email = event.getEmail();
        this.mobileNumber = event.getMobileNumber();
        //this.address = event.getAddress();
    }

    @CommandHandler
    public void handle(CreateOrderCommand.DeleteCustomerCommand command) {
        apply(new CustomerDeletedEvent(command.getId()));
    }

    @EventSourcingHandler
    public void on(CustomerDeletedEvent event) {
        this.id = null;
    }
}

