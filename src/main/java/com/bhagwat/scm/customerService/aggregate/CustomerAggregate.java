package com.bhagwat.scm.customerService.aggregate;

import com.bhagwat.scm.customerService.dto.*;
import com.bhagwat.scm.customerService.entity.Address;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

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
    private Address address;

    public CustomerAggregate() {}

    @CommandHandler
    public CustomerAggregate(CreateCustomerCommand command) {
        apply(new CustomerCreatedEvent(command.getId(), command.getFname(), command.getMname(),
                command.getLname(), command.getEmail(), command.getMobileNumber(), command.getAddress()));
    }

    @EventSourcingHandler
    public void on(CustomerCreatedEvent event) {
        this.id = event.getId();
        this.fname = event.getFname();
        this.mname = event.getMname();
        this.lname = event.getLname();
        this.email = event.getEmail();
        this.mobileNumber = event.getMobileNumber();
        this.address = event.getAddress();
    }

    @CommandHandler
    public void handle(UpdateCustomerCommand command) {
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
        this.address = event.getAddress();
    }

    @CommandHandler
    public void handle(DeleteCustomerCommand command) {
        apply(new CustomerDeletedEvent(command.getId()));
    }

    @EventSourcingHandler
    public void on(CustomerDeletedEvent event) {
        this.id = null;
    }
}

