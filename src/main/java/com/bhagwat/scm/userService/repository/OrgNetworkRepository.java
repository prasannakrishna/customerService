package com.bhagwat.scm.userService.repository;

import com.bhagwat.scm.userService.entity.OrgNetwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgNetworkRepository extends JpaRepository<OrgNetwork, String> {}