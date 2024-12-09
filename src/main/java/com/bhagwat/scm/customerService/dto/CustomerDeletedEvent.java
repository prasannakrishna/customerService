package com.bhagwat.scm.customerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerDeletedEvent {
    private String id;
}

