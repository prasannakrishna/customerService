package com.bhagwat.scm.customerService.command.commanddto;

import com.bhagwat.scm.customerService.command.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    private final String orderId;
    private final String customerId;
    private final LocalDateTime orderCreatedDate;
    private final String consignmentId;

    // Getters

    @Data
    @AllArgsConstructor
    public static class DeleteCustomerCommand {
        @TargetAggregateIdentifier
        private String id;
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateCustomerCommand {
        @TargetAggregateIdentifier
        private String id;
        private String fname;
        private String mname;
        private String lname;
        private String email;
        private String mobileNumber;
        private Address address;
    }
}
