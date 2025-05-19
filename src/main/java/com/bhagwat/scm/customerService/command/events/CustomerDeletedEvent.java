package com.bhagwat.scm.customerService.command.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerDeletedEvent {
    private String id;
}

