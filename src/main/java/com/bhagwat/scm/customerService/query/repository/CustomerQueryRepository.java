package com.bhagwat.scm.customerService.query.repository;

import com.bhagwat.scm.customerService.query.entity.CustomerView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public interface CustomerQueryRepository extends JpaRepository<CustomerView, Long> {

}
