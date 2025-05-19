package com.bhagwat.scm.customerService.query.projection;

import com.bhagwat.scm.customerService.dto.AddressDto;
import com.bhagwat.scm.customerService.dto.AddressRequest;
import com.bhagwat.scm.customerService.command.entity.Address;
import com.bhagwat.scm.customerService.command.entity.Customer;
import com.bhagwat.scm.customerService.command.events.CustomerCreatedEvent;
import com.bhagwat.scm.customerService.command.repository.AddressCommandRepository;
import com.bhagwat.scm.customerService.command.repository.CustomerCommandRepository;
import com.bhagwat.scm.customerService.query.entity.AddressView;
import com.bhagwat.scm.customerService.query.entity.CustomerView;
import com.bhagwat.scm.customerService.query.repository.AddressQueryRepository;
import com.bhagwat.scm.customerService.query.repository.CustomerQueryRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomerProjection {
    private final CustomerQueryRepository customerQueryRepository;
    private final AddressQueryRepository addressRepository;

    @EventHandler
    public void on(CustomerCreatedEvent event) {
        CustomerView customer = new CustomerView();
        customer.setFname(event.getFname());
        customer.setMname(event.getMname());
        customer.setLname(event.getLname());
        customer.setEmail(event.getEmail());
        customer.setMobileNumber(event.getMobileNumber());
        AddressDto addressDto = (AddressDto) event.getAddresses();
        List<AddressView> listAddress = new ArrayList<>();
        AddressView address = new AddressView();
        address.setAddressLine2(addressDto.getAddressLine2());
        listAddress.add(address);

        customer.setAddresses(listAddress);
        customerQueryRepository.save(customer);

        List<AddressDto> addressRequests = event.getAddresses();
        boolean singleAddress = addressRequests.size() == 1;

        addressRequests.forEach(req -> {
            AddressView add = new AddressView();
            add.setCustomer(customer);
            add.setAddressLine1(req.getAddressLine1());
            add.setCity(req.getCity());
            add.setState(req.getState());
            add.setCountry(req.getCountry());
            add.setPincode(req.getPincode());
            add.setPrimaryAddress(singleAddress); // only one => mark it primary
            addressRepository.save(add);
        });
    }
}
