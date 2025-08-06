package com.bhagwat.scm.userService.repository;

import com.bhagwat.scm.userService.entity.ApplicationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApplicationSubscriptionRepository extends JpaRepository<ApplicationSubscription, String> {
    List<ApplicationSubscription> findByOrgOrgIdAndIsActiveTrueAndRenewalDateAfter(String orgId, LocalDate currentDate);
}
