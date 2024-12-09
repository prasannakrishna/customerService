package com.bhagwat.scm.customerService.dto;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteCustomerCommand {
    @TargetAggregateIdentifier
    private String id;
}

