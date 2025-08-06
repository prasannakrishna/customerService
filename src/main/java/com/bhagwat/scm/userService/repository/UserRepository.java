package com.bhagwat.scm.userService.repository;

import com.bhagwat.scm.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    long countByOrgOrgIdAndIsActiveTrue(String orgId);
}
