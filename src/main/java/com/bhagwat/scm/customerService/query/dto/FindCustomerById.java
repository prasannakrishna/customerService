package com.bhagwat.scm.customerService.query.dto;

public class FindCustomerById {
    private final Long id;

    public FindCustomerById(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
