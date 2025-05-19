package com.bhagwat.scm.customerService.command.repository;

import com.bhagwat.scm.customerService.command.entity.Address;
import com.bhagwat.scm.customerService.command.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressCommandRepository extends JpaRepository<Address, Long> {
    @Query("SELECT DISTINCT a.customer FROM Address a WHERE a.pincode = :pincode AND a.isPrimaryAddress = true")
    List<Customer> findDistinctCustomersByPincodeAndPrimaryTrue(@Param("pincode") String pincode);

    @Query("SELECT DISTINCT a.customer FROM Address a WHERE a.pincode = :pincode")
    List<Customer> findDistinctCustomersByPincode(@Param("pincode") String pincode);

    @Query("SELECT DISTINCT a.customer FROM Address a WHERE a.pincode = :pincode AND (:onlyPrimaryAddress = false OR a.isPrimaryAddress = true)")
    List<Customer> findDistinctCustomersByPincodeAndPrimaryFlag(@Param("pincode") String pincode,
                                                                      @Param("onlyPrimaryAddress") boolean onlyPrimaryAddress);

}
