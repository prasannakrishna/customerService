package com.bhagwat.scm.userService.repository;

import com.bhagwat.scm.userService.entity.Org;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgRepository extends JpaRepository<Org, String> {}